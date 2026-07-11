package com.orderplatform.shipping_service.kafka;

import com.orderplatform.shipping_service.config.KafkaTopics;
import com.orderplatform.shipping_service.event.ShipmentCreatedEvent;
import com.orderplatform.shipping_service.event.ShipmentDeliveredEvent;
import com.orderplatform.shipping_service.event.ShipmentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PreDestroy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentRequestConsumer {

    private final ShippingProducer shippingProducer;

    private final ScheduledExecutorService deliveryScheduler =
            Executors.newSingleThreadScheduledExecutor();

    @KafkaListener(
            topics = KafkaTopics.SHIPMENT_REQUESTED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ShipmentRequestedEvent event) {
        String trackingNumber = "TRL-" + UUID.randomUUID();

        log.info(
                "Creating shipment: orderId={}, trackingNumber={}",
                event.orderId(),
                trackingNumber
        );

        shippingProducer.sendShipmentCreatedEvent(
                new ShipmentCreatedEvent(
                        event.orderId(),
                        trackingNumber
                )
        );

        deliveryScheduler.schedule(
                () -> deliver(event.orderId()),
                5,
                TimeUnit.SECONDS
        );
    }

    private void deliver(Long orderId) {
        shippingProducer.sendShipmentDeliveredEvent(
                new ShipmentDeliveredEvent(orderId)
        );
        log.info(
                "Shipment delivered: orderId={}",
                orderId
        );
    }

    @PreDestroy
    void shutdownScheduler() {
        deliveryScheduler.shutdown();
    }
}
