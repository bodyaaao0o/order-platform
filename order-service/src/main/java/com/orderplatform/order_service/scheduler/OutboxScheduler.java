package com.orderplatform.order_service.scheduler;

import com.orderplatform.order_service.config.KafkaTopics;

import com.orderplatform.order_service.entity.OutboxEvent;

import com.orderplatform.order_service.event.InventoryReserveRequestEvent;

import com.orderplatform.order_service.event.OrderCreatedEvent;

import com.orderplatform.order_service.kafka.OrderProducer;

import com.orderplatform.order_service.repository.OutboxEventRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;

    private final OrderProducer orderProducer;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void processOutboxEvents() {

        List<OutboxEvent> events =
                outboxEventRepository.findByProcessedFalse();

        for (OutboxEvent event : events) {

            try {

                publishEvent(event);

                event.setProcessed(true);

                outboxEventRepository.save(event);

                log.info(
                        "Outbox event processed: {}",
                        event.getId()
                );

            } catch (Exception e) {

                log.error(
                        "Failed to process outbox event: {}",
                        event.getId(),
                        e
                );
            }
        }
    }

    private void publishEvent(OutboxEvent event) throws Exception {
        if (KafkaTopics.ORDER_CREATED.equals(event.getEventType())) {
            OrderCreatedEvent orderEvent =
                    objectMapper.readValue(
                            event.getPayload(),
                            OrderCreatedEvent.class
                    );

            orderProducer.sendOrderCreatedEvent(orderEvent);
            return;
        }

        if (KafkaTopics.INVENTORY_RESERVE_REQUESTED.equals(event.getEventType())) {
            InventoryReserveRequestEvent inventoryEvent =
                    objectMapper.readValue(
                            event.getPayload(),
                            InventoryReserveRequestEvent.class
                    );

            orderProducer.sendInventoryReserveRequestEvent(inventoryEvent);
            return;
        }

        throw new IllegalArgumentException(
                "Unsupported outbox event type: " + event.getEventType()
        );
    }
}
