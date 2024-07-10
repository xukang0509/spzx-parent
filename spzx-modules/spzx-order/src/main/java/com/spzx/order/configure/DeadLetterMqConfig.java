package com.spzx.order.configure;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class DeadLetterMqConfig {
    // 声明一些变量
    public static final String EXCHANGE_DEAD = "exchange.dead";
    public static final String ROUTING_DEAD_1 = "routing.dead.1";
    public static final String ROUTING_DEAD_2 = "routing.dead.2";
    public static final String QUEUE_DEAD_1 = "queue.dead.1";
    public static final String QUEUE_DEAD_2 = "queue.dead.2";

    // 定义交换机
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_DEAD, true, false, null);
    }

    @Bean
    public Queue queue1() {
        // 设置如果队列一 出现问题，则通过参数转到EXCHANGE_DEAD，ROUTING_DEAD_2 上
        HashMap<String, Object> map = new HashMap<>();
        // 参数绑定，此处的 key 固定，不能随便写
        map.put("x-dead-letter-exchange", EXCHANGE_DEAD);
        map.put("x-dead-letter-routing-key", ROUTING_DEAD_2);
        // 设置延迟时间
        map.put("x-message-ttl", 10 * 1000);
        // 队列名称，是否持久化，是否独享、排外的【true：只可以在本次连接中访问】，是否自动删除，队列的其他属性参数
        return new Queue(QUEUE_DEAD_1, true, false, false, map);
    }

    @Bean
    public Binding binding1(Exchange exchange, Queue queue1) {
        // 将队列1 通过 ROUTING_DEAD_1 key 绑定到 EXCHANGE_DEAD 交换机上
        return BindingBuilder.bind(queue1).to(exchange).with(ROUTING_DEAD_1).noargs();
    }

    // 队列二 是一个普通队列
    @Bean
    public Queue queue2() {
        return new Queue(QUEUE_DEAD_2, true, false, false, null);
    }

    // 设置队列二的绑定规格
    @Bean
    public Binding binding2(Exchange exchange, Queue queue2) {
        return BindingBuilder.bind(queue2).to(exchange).with(ROUTING_DEAD_2).noargs();
    }
}
