package com.spzx.order.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.configure.DeadLetterMqConfig;
import com.spzx.order.configure.DelayedMqConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Tag(name = "RabbitMQ接口管理")
@Slf4j
@RestController
@RequestMapping("/mq")
public class MqController extends BaseController {
    @Resource
    private RabbitService rabbitService;

    @Operation(summary = "发送消息")
    @GetMapping("/sendMessage")
    public AjaxResult sendMessage() {
        rabbitService.sendMessage(MqConst.EXCHANGE_TEST, MqConst.ROUTING_TEST, "Hello MQ");
        return success();
    }

    @Operation(summary = "发送确认消息")
    @GetMapping("/sendConfirmMessage")
    public AjaxResult sendConfirmMessage() {
        rabbitService.sendMessage(MqConst.EXCHANGE_TEST, MqConst.ROUTING_CONFIRM, "Hello MQ Confirm");
        return success();
    }

    @Operation(summary = "发送延迟消息：基于死信实现")
    @GetMapping("/sendDeadLetterMessage")
    public AjaxResult sendDeadLetterMessage() {
        log.info("生产者发送延迟消息(基于死信队列)，时间：{}", new Date());
        rabbitService.sendMessage(DeadLetterMqConfig.EXCHANGE_DEAD, DeadLetterMqConfig.ROUTING_DEAD_1, "Hello MQ DeadLetter DelayLetter");
        return success();
    }

    @Operation(summary = "发送延迟消息：基于延迟插件")
    @GetMapping("/sendDelayMessage")
    public AjaxResult sendDelayMessage() {
        // 调用工具方法发送延迟消息
        int delayTime = 15;
        log.info("生产者发送延迟消息(基于延迟插件)，时间：{}", new Date());
        rabbitService.sendDelayMessage(DelayedMqConfig.EXCHANGE_DELAY, DelayedMqConfig.ROUTING_DELAY, "Hello MQ DelayLetter", delayTime);
        return success();
    }
}
