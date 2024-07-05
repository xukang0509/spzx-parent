package com.spzx.common.redis.cache;

import java.lang.annotation.*;

/**
 * 新增自定义注解，该注解作用于业务方法上，对方法功能逻辑进行增强
 * 1.优先从缓存中获取数据
 * 2.获取分布式锁，执行业务(查询数据库)，释放锁逻辑等
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisCache {
    /**
     * 添加注解中方法(注解属性)，通过注解属性指定放入缓存业务数据前缀、后缀
     */
    String prefix() default "data";
}
