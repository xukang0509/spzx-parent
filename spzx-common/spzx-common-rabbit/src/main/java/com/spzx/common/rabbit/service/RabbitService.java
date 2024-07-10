package com.spzx.common.rabbit.service;

import com.alibaba.fastjson2.JSON;
import com.spzx.common.rabbit.entity.GuiguCorrelationData;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RabbitService {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 发送消息
     *
     * @param exchange   交换机
     * @param routingKey 路由key
     * @param message    消息
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        // 1.创建自定义相关消息对象---包含业务数据本身、交换机名称、路由键、队列类型、延迟时间、重试次数
        GuiguCorrelationData correlationData = new GuiguCorrelationData();
        String uuid = "mq:" + UUID.randomUUID().toString().replaceAll("-", "");
        correlationData.setId(uuid);
        correlationData.setMessage(message);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);

        // 2.将相关数据封装到发送消息方法中
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);

        // 3.将相关数据存入Redis；key：uuid，value：相关消息对象；10分钟
        redisTemplate.opsForValue().set(uuid, JSON.toJSONString(correlationData), 10, TimeUnit.MINUTES);
        return true;
    }

    /**
     * 发送延迟消息方法
     *
     * @param exchange   交换机
     * @param routingKey 路由key
     * @param msg        消息
     * @param delayTime  延迟时间，单位：秒
     */
    public boolean sendDelayMessage(String exchange, String routingKey, Object msg, int delayTime) {
        // 1.创建自定义相关消息对象---包含业务数据本身、交换机名称、路由键、队列类型、延迟时间、重试次数
        GuiguCorrelationData correlationData = new GuiguCorrelationData();
        String uuid = "mq:" + UUID.randomUUID().toString().replaceAll("-", "");
        correlationData.setId(uuid);
        correlationData.setMessage(msg);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        correlationData.setDelay(true);
        correlationData.setDelayTime(delayTime);

        // 2.将相关数据封装到发送消息方法中
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, (message) -> {
            message.getMessageProperties().setDelay(delayTime * 1000);
            return message;
        }, correlationData);

        // 3.将相关数据存入Redis；key：uuid，value：相关消息对象；10分钟
        redisTemplate.opsForValue().set(uuid, JSON.toJSONString(correlationData), 10, TimeUnit.MINUTES);
        return true;
    }
}
