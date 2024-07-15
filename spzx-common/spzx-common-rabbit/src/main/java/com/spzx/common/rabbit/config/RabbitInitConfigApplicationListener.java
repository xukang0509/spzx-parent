package com.spzx.common.rabbit.config;

import com.alibaba.fastjson2.JSON;
import com.spzx.common.rabbit.entity.GuiguCorrelationData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RabbitInitConfigApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.setupCallbacks();
    }

    private void setupCallbacks() {
        /*
         * 只确认消息是否正确到达 Exchange 中，成功与否都会回调
         * @param correlationData 相关数据，非消息本身业务数据
         * @param ack             应答结果
         * @param reason          如果消息发送到交换机失败，错误原因
         */
        this.rabbitTemplate.setConfirmCallback((correlationData, ack, reason) -> {
            if (ack) {
                // 消息到交换器成功
                log.info("消息发送到Exchange成功：{}", correlationData);
            } else {
                // 消息到交换器失败
                log.error("消息发送到Exchange失败：{}", reason);
                // 执行消息重发
                this.retrySendMsg(correlationData);
            }
        });

        /*
         * 消息没有正确到达队列时触发回调，如果正确到达队列不执行
         */
        this.rabbitTemplate.setReturnsCallback(returned -> {
            log.error("returned：{}", returned.getMessage());
            log.error("replyCode：{}", returned.getReplyCode());
            log.error("replyText：{}", returned.getReplyText());
            log.error("exchange：{}", returned.getExchange());
            log.error("routingKey：{}", returned.getRoutingKey());

            // 当路由队列失败 也需要重发
            // 1.构建相关数据对象
            String redisKey = returned.getMessage().getMessageProperties().getHeader("spring_returned_message_correlation");
            String correlationDataStr = stringRedisTemplate.opsForValue().get(redisKey);
            GuiguCorrelationData correlationData = JSON.parseObject(correlationDataStr, GuiguCorrelationData.class);

            // TODO 方式一：如果不考虑延迟消息重发，直接返回
            if (correlationData.isDelay()) return;

            // 2.调用消息重发方法
            this.retrySendMsg(correlationData);
        });
    }

    /**
     * 消息重新发送
     *
     * @param correlationData
     */
    private void retrySendMsg(CorrelationData correlationData) {
        // 获取相关数据
        GuiguCorrelationData guiguCorrelationData = (GuiguCorrelationData) correlationData;

        // 获取redis中存放重试次数
        int retryCount = guiguCorrelationData.getRetryCount();
        if (retryCount >= 3) {
            log.error("生产者超过最大重试次数，将失败的消息存入数据库用人工处理；给管理员发送邮件；给管理员发送短信；");
            return;
        }
        // 重次次数加一
        retryCount++;
        guiguCorrelationData.setRetryCount(retryCount);
        stringRedisTemplate.opsForValue().set(guiguCorrelationData.getId(), JSON.toJSONString(guiguCorrelationData),
                10, TimeUnit.MINUTES);
        log.info("进行消息重发！");

        // TODO 方式二：如果是延迟消息，依然需要设置消息延迟时间
        if (guiguCorrelationData.isDelay()) {
            // 重发延迟消息
            //rabbitTemplate.convertAndSend(
            //        guiguCorrelationData.getExchange(),
            //        guiguCorrelationData.getRoutingKey(),
            //        guiguCorrelationData.getMessage(),
            //        (message) -> {
            //            message.getMessageProperties().setDelay(guiguCorrelationData.getDelayTime() * 1000);
            //            return message;
            //        }, guiguCorrelationData);
        } else {
            // 重发普通消息
            rabbitTemplate.convertAndSend(
                    guiguCorrelationData.getExchange(),
                    guiguCorrelationData.getRoutingKey(),
                    guiguCorrelationData.getMessage(),
                    guiguCorrelationData);
        }
    }
}
