package com.spzx.product.service.impl;

import com.spzx.common.core.utils.StringUtils;
import com.spzx.product.service.TestService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements TestService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 采用SpringDataRedis实现分布式锁
     * 原理：执行业务方法之前先尝试获取锁(set nx存入key value)，如果获取成功再执行业务代码，业务执行完毕后将锁释放(del key)
     */
    @Override
    public void testLock() {
        // 0.先尝试获取锁 setnx key val
        String uuid = UUID.randomUUID().toString();
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        if (flag) {
            // 获取锁成功，执行业务代码
            // 1.先从redis中通过key num获取值  key提前手动设置 num 初始值：0
            String value = this.stringRedisTemplate.opsForValue().get("num");
            // 2.如果值为空则非法直接返回即可
            if (StringUtils.isBlank(value)) {
                return;
            }
            // 3.对num值进行自增加一
            int num = Integer.parseInt(value);
            this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));
            // 4.将锁释放 判断uuid
            // 问题：删除操作缺乏原子性
            // if (uuid.equals(stringRedisTemplate.opsForValue().get("lock"))) {
            //     stringRedisTemplate.delete("lock");
            // }

            // 执行脚本参数，参数1：脚本对象封装lua脚本，参数二：lua脚本中需要key参数(KEY[i])，参数三：lua脚本中需要参数值 ARGV[i]
            // 4.1 先创建脚本对象 DefaultRedisScript泛型脚本语言返回值类型，Long 0：失败 1：成功
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            // 4.2 设置脚本文本
            String script =
                    "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                            "then\n" +
                            "   return redis.call(\"del\", KEYS[1])\n" +
                            "else\n" +
                            "   return 0\n" +
                            "end";
            redisScript.setScriptText(script);
            // 4.3设置响应类型
            redisScript.setResultType(Long.class);
            stringRedisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);
        } else {
            try {
                Thread.sleep(100);
                // 自旋重试
                this.testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}