package com.spzx.product.receiver;

import com.rabbitmq.client.Channel;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.product.service.ProductService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductReceiver {
    @Resource
    private ProductService productService;

    /**
     * 解锁库存
     *
     * @param orderNo 订单号
     */
    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = MqConst.EXCHANGE_PRODUCT, durable = "true"),
                    value = @Queue(value = MqConst.QUEUE_UNLOCK, durable = "true"),
                    key = MqConst.ROUTING_UNLOCK
            )
    )
    public void unlock(String orderNo, Message message, Channel channel) {
        // 业务处理
        if (StringUtils.isNotEmpty(orderNo)) {
            log.info("【商品服务】监听解锁库存信息：{}", orderNo);
            // 解锁库存
            productService.unlock(orderNo);
        }
        // 手动应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 扣减库存
     *
     * @param orderNo 订单号
     */
    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = MqConst.EXCHANGE_PRODUCT, durable = "true"),
                    value = @Queue(value = MqConst.QUEUE_MINUS, durable = "true"),
                    key = MqConst.ROUTING_MINUS
            )
    )
    public void minus(String orderNo, Message message, Channel channel) {
        // 业务处理
        if (StringUtils.isNotEmpty(orderNo)) {
            log.info("【商品服务】监听减库存消息：{}", orderNo);
            // 扣减库存
            productService.minus(orderNo);
        }
        // 手动应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
