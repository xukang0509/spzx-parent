package com.spzx.common.redis.cache;

import com.alibaba.fastjson2.JSON;
import com.spzx.common.core.utils.uuid.UUID;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存+分布式锁功能对自定义注解增强切面类
 */
@Slf4j
@Aspect
@Component
public class RedisCacheAspect {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 环绕通知：对所有业务模块任意方法使用自定义注解缓存方法进行增强
     * 增强逻辑：
     * 1.优先从缓存redis中获取业务数据
     * 2.未命中缓存，获取分布式锁
     * 3.执行目标方法(查询数据库方法)
     * 4.将锁释放
     */
    @SneakyThrows
    @Around("@annotation(redisCache)")
    public Object redisCacheAdvice(ProceedingJoinPoint proceedingJoinPoint, RedisCache redisCache) {
        try {
            // 1.优先从redis缓存中获取业务数据
            // 1.1 构建业务数据key 形式：缓存注解中前缀+方法参数
            // 1.1.1 获取缓存注解前缀
            String prefix = redisCache.prefix();
            // 1.1.2 获取执行目标方法参数
            String paramVal = "";
            Object[] args = proceedingJoinPoint.getArgs();
            if (args != null && args.length > 0) {
                paramVal = Arrays.asList(args).stream().map(JSON::toJSONString)
                        .collect(Collectors.joining(":"));
            }
            String cacheKey = prefix + paramVal;
            // 1.2 查询Redis缓存中业务数据
            Object resultObject = redisTemplate.opsForValue().get(cacheKey);
            if (resultObject != null) {
                log.info("命中缓存，直接返回，KEY：{}，线程ID：{}，线程名称：{}", cacheKey,
                        Thread.currentThread().getId(), Thread.currentThread().getName());
                // 1.3 命中缓存直接返回即可
                return resultObject;
            }

            // 2.获取分布式锁 解决缓存击穿问题
            // 2.1 构建锁key
            String lockKey = redisCache.lockPrefix() + cacheKey;
            // 2.2 采用uuid作为线程标识
            String lockVal = UUID.randomUUID().toString().replaceAll("-", "");
            boolean flag = getLock(lockKey, lockVal, redisCache);
            if (flag) {
                // 3.执行目标方法
                try {
                    // 3.1 再次查询一次缓存：处于阻塞等待获取线程(终将获取锁成功) 避免获取锁线程再次查库，这里再查一次缓存
                    resultObject = redisTemplate.opsForValue().get(cacheKey);
                    if (resultObject != null) {
                        log.info("命中缓存，直接返回，KEY：{}，线程ID：{}，线程名称：{}", cacheKey,
                                Thread.currentThread().getId(), Thread.currentThread().getName());
                        return resultObject;
                    }
                    // 3.2 未命中缓存，执行查询数据库(目标方法)
                    resultObject = proceedingJoinPoint.proceed();
                    // 3.3 将查询数据库结果放入缓存
                    if (resultObject == null && redisCache.isSaveNull()) {
                        // 存空值 解决缓存穿透问题
                        redisTemplate.opsForValue().set(cacheKey, resultObject,
                                redisCache.saveNullValueTime(), TimeUnit.SECONDS);
                    } else if (resultObject != null) {
                        // 存非空数据
                        // 添加随机数过期时间，解决缓存雪崩问题
                        long added = new Random().nextLong(redisCache.random());
                        redisTemplate.opsForValue().set(cacheKey, resultObject,
                                redisCache.saveNonNullValueTime() + added, TimeUnit.SECONDS);
                    }
                    return resultObject;
                } finally {
                    releaseLock(lockKey, lockVal);
                }
            }
        } catch (Throwable e) {
            log.info("自定义缓存切面异常：{}", e);
            e.printStackTrace();
        }
        // 5.兜底方案：如果Redis服务不可用，则执行查询数据方法
        return proceedingJoinPoint.proceed();
    }

    private boolean getLock(String lockKey, String lockVal, RedisCache redisCache) {
        // 2.3 利用Redis提供set nx ex 获取分布式锁
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(lockKey, lockVal, redisCache.lockExpire(), TimeUnit.SECONDS);
        if (flag) {
            log.info("获取锁成功，KEY：{}，线程ID：{}，线程名称：{}", lockKey, Thread.currentThread().getId(),
                    Thread.currentThread().getName());
            renewExpire(lockKey, lockVal, redisCache.lockExpire(), TimeUnit.SECONDS);
            return true;
        } else {
            try {
                log.error("获取锁失败，自旋，KEY：{}，线程ID：{}，线程名称：{}", lockKey, Thread.currentThread().getId(),
                        Thread.currentThread().getName());
                Thread.sleep(50);
                return getLock(lockKey, lockVal, redisCache);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private void releaseLock(String lockKey, String lockVal) {
        // 4.业务执行完毕 释放锁
        String scriptText = """
                if redis.call("get",KEYS[1]) == ARGV[1]
                then
                    return redis.call("del", KEYS[1])
                else
                    return 0
                end
                """;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(scriptText, Long.class);
        redisTemplate.execute(redisScript, Arrays.asList(lockKey), lockVal);
    }

    private void renewExpire(String lockKey, String lockVal, long expire, TimeUnit unit) {
        threadPoolExecutor.execute(() -> {
            try {
                Thread.sleep(expire * 2000 / 3);
                String script = """
                        if redis.call("get",KEYS[1]) == ARGV[1]
                        then
                            return 1
                        else
                            return 0
                        end""";
                DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>(script, Boolean.class);
                Boolean flag = (Boolean) redisTemplate.execute(redisScript, Arrays.asList(lockKey), lockVal);
                while (flag) {
                    redisTemplate.opsForValue().setIfAbsent(lockKey, lockVal, expire, unit);
                    Thread.sleep(expire * 2000 / 3);
                    flag = (Boolean) redisTemplate.execute(redisScript, Arrays.asList(lockKey), lockVal);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
