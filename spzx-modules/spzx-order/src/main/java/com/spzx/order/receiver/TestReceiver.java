package com.spzx.order.receiver;

import com.rabbitmq.client.Channel;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.order.configure.DeadLetterMqConfig;
import com.spzx.order.configure.DelayedMqConfig;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TestReceiver {
    @Resource
    private RedisTemplate redisTemplate;


    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = MqConst.EXCHANGE_TEST, durable = "true"),
                    value = @Queue(value = MqConst.QUEUE_TEST, durable = "true"),
                    key = MqConst.ROUTING_TEST
            )
    )
    public void test(String content, Message message) {
        log.info("接收消息：{}", content);
        log.info("接收消息：{}", new String(message.getBody()));
    }

    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = MqConst.EXCHANGE_TEST, durable = "true"),
                    value = @Queue(value = MqConst.QUEUE_CONFIRM, durable = "true"),
                    key = MqConst.ROUTING_CONFIRM
            )
    )
    public void confirm(String content, Message message, Channel channel) {
        log.info("接收确认消息：{}", content);
        // false 确认一个消息；true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = DeadLetterMqConfig.QUEUE_DEAD_2)
    public void getDeadLetterMsg(String content, Message message, Channel channel) {
        log.info("死信消费者消费消息：{}, 时间：{}", content, new Date());
        // false 确认一个消息；true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @SneakyThrows
    @RabbitListener(queues = DelayedMqConfig.QUEUE_DELAY_1)
    public void getDelayLetterMsg(String content, Message message, Channel channel) {
        // 接收消息，消费者端判断是否需要做幂等性处理
        // 如果业务保证幂等性，基于 redis setnx 保证
        String key = "mq:" + content;
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, "", 200, TimeUnit.SECONDS);
        if (!flag) {
            // 说明该业务数据及时被执行
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("消息：{}，被及时处理", content);
            return;
        }
        log.info("延迟消费者消费消息：{}, 时间：{}", content, new Date());
        // false 确认一个消息；true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
