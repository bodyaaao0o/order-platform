package com.orderplatform.order_service.kafka;


import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.event.InventoryReleaseRequestedEvent;
import com.orderplatform.order_service.event.InventoryReserveRequestEvent;
import com.orderplatform.order_service.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent( OrderCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event);
    }

    public void sendInventoryReserveRequestEvent(InventoryReserveRequestEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVE_REQUESTED, event);
    }

    public void sendInventoryReleaseRequestedEvent(InventoryReleaseRequestedEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_RELEASE_REQUESTED, event);
//        log.info(
//                "Inventory release requested event sent: orderId={}",
//                event.orderId()
//        );
    }
}
