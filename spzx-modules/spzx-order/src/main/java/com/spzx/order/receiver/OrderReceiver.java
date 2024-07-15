package com.spzx.order.receiver;


import com.rabbitmq.client.Channel;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.service.OrderInfoService;
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
public class OrderReceiver {
    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private RabbitService rabbitService;

    /**
     * 延迟关闭订单消费者
     *
     * @param orderId orderId
     */
    @SneakyThrows
    @RabbitListener(queues = MqConst.QUEUE_CANCEL_ORDER)
    public void processCloseOrder(String orderId, Message message, Channel channel) {
        // 1.处理业务
        if (StringUtils.isNotEmpty(orderId)) {
            log.info("【订单微服务】关闭订单消息：{}", orderId);
            // 修改订单状态-为关闭
            orderInfoService.processCloseOrder(Long.parseLong(orderId));
            // 发送MQ消息通知支付系统关闭可能产生本地交易记录跟支付宝交易记录
            OrderInfo orderInfo = orderInfoService.getById(orderId);
            rabbitService.sendMessage(MqConst.EXCHANGE_PAYMENT_PAY, MqConst.ROUTING_PAYMENT_CLOSE,
                    orderInfo.getOrderNo());
        }
        // 2.手动应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 监听订单支付成功消息：更新订单状态；扣减商品库存
     *
     * @param orderNo 订单号
     */
    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = MqConst.EXCHANGE_PAYMENT_PAY, durable = "true"),
                    value = @Queue(value = MqConst.QUEUE_PAYMENT_PAY, durable = "true"),
                    key = MqConst.ROUTING_PAYMENT_PAY
            )
    )
    public void processPaySuccess(String orderNo, Message message, Channel channel) {
        // 1.处理业务
        if (StringUtils.isNotEmpty(orderNo)) {
            log.info("【订单微服务】监听订单支付成功消息：{}", orderNo);
            // 更新订单支付状态
            orderInfoService.processPaySuccess(orderNo);
            // 基于MQ通知扣减库存
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT, MqConst.ROUTING_MINUS, orderNo);
        }
        // 2.手动应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
