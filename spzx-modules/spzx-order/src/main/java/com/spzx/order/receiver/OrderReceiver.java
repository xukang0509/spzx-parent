package com.spzx.order.receiver;


import com.rabbitmq.client.Channel;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.order.service.OrderInfoService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderReceiver {
    @Resource
    private OrderInfoService orderInfoService;


    @SneakyThrows
    @RabbitListener(queues = MqConst.QUEUE_CANCEL_ORDER)
    public void processCloseOrder(String orderId, Message message, Channel channel) {
        // 1.处理业务
        if (orderId != null) {
            log.info("【订单微服务】关闭订单消息：{}", orderId);
            orderInfoService.processCloseOrder(Long.parseLong(orderId));
        }
        // 2.手动应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
