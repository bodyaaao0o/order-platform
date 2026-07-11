package com.orderplatform.shipping_service.kafka;


import com.orderplatform.shipping_service.config.KafkaTopics;
import com.orderplatform.shipping_service.event.ShipmentCreatedEvent;
import com.orderplatform.shipping_service.event.ShipmentDeliveredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendShipmentCreatedEvent(ShipmentCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.SHIPMENT_CREATED, event);
    }

    public void sendShipmentDeliveredEvent(ShipmentDeliveredEvent event) {
        kafkaTemplate.send(KafkaTopics.SHIPMENT_DELIVERED, event);
    }
}
