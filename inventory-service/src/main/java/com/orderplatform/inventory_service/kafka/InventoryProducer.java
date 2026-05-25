package com.orderplatform.inventory_service.kafka;


import com.orderplatform.inventory_service.config.KafkaTopics;
import com.orderplatform.inventory_service.event.InventoryFailedEvent;
import com.orderplatform.inventory_service.event.InventoryReservedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendInventoryReservedEvent(InventoryReservedEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, event);
    }

    public void sendInventoryFailedEvent(InventoryFailedEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_FAILED, event);
    }
}
