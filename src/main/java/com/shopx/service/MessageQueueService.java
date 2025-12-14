package com.shopx.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * 极简消息队列服务
 */
@Slf4j
@Service
public class MessageQueueService {

    @RabbitListener(queues = "shopx.order.queue")
    public void handleOrderMessage(String message) {
        log.info("处理订单消息: {}", message);
    }

    @RabbitListener(queues = "shopx.notification.queue")
    public void handleNotificationMessage(String message) {
        log.info("处理通知消息: {}", message);
    }
}