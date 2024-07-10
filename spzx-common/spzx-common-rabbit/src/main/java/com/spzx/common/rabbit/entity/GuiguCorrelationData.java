package com.spzx.common.rabbit.entity;

import lombok.Data;
import org.springframework.amqp.rabbit.connection.CorrelationData;

@Data
public class GuiguCorrelationData extends CorrelationData {
    // 消息体
    private Object message;
    // 交换机
    private String exchange;
    // 路由key
    private String routingKey;
    // 重试次数
    private int retryCount = 0;
    // 是否延迟消息
    private boolean isDelay = false;
    // 延时时长
    private int delayTime = 10;
}
