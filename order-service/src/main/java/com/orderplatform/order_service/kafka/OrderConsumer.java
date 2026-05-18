package com.orderplatform.order_service.kafka;


import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.event.OrderCreatedEvent;
import com.orderplatform.order_service.service.NotificationService;
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
            groupId = "order-group"
    )
    public void consumerOrderCreateEvent(OrderCreatedEvent event) {
        log.info(
                "Received order created event: {}",
                event
        );

//        notificationService.sendOrderCreatedEmail(event.customerEmail(), event.orderId());
        throw new RuntimeException("Kafka consumer failed");
    }
}
