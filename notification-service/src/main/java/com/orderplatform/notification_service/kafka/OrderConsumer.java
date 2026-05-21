package com.orderplatform.notification_service.kafka;

import com.orderplatform.notification_service.config.KafkaTopics;

import com.orderplatform.notification_service.event.OrderCreatedEvent;

import com.orderplatform.notification_service.service.NotificationService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "notification-group"
    )
    public void consumeOrderCreatedEvent(
            OrderCreatedEvent event
    ) {

        log.info(
                "Received order event: {}",
                event
        );

        notificationService.sendOrderCreatedEmail(
                event.customerEmail(),
                event.orderId()
        );
    }
}
