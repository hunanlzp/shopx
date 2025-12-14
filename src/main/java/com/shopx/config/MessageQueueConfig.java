package com.shopx.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 极简消息队列配置
 */
@Configuration
public class MessageQueueConfig {

    @Bean
    public Queue orderQueue() {
        return new Queue("shopx.order.queue", true);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue("shopx.notification.queue", true);
    }
}