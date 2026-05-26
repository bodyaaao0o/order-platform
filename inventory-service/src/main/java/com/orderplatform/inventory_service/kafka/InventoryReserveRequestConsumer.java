package com.orderplatform.inventory_service.kafka;

import com.orderplatform.inventory_service.config.KafkaTopics;
import com.orderplatform.inventory_service.event.*;
import com.orderplatform.inventory_service.exception.InsufficientStockException;
import com.orderplatform.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReserveRequestConsumer {

    private final InventoryService inventoryService;

    private final InventoryProducer inventoryProducer;

    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    @KafkaListener(
            topics = KafkaTopics.INVENTORY_RESERVE_REQUESTED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            InventoryReserveRequestEvent event
    ) {

        try {

            processedEventRepository.save(

                    ProcessedEvent.builder()

                            .eventId(
                                    event.eventId().toString()
                            )

                            .processedAt(
                                    LocalDateTime.now(ZoneOffset.UTC)
                            )

                            .build()
            );

        } catch (DataIntegrityViolationException e) {

            log.warn(
                    "Event already processed: {}",
                    event.eventId()
            );

            return;
        }

        log.info(
                "Received inventory reserve request: orderId={}, sku={}, quantity={}",
                event.orderId(),
                event.sku(),
                event.quantity()
        );

        try {

            inventoryService.reserveStock(
                    event.orderId(),
                    event.sku(),
                    event.quantity()
            );

            inventoryProducer.sendInventoryReservedEvent(
                    new InventoryReservedEvent(
                            event.orderId(),
                            event.sku(),
                            event.quantity()
                    )
            );

            inventoryProducer.sendPaymentRequestedEvent(

                    new PaymentRequestedEvent(

                            event.orderId(),

                            event.customerEmail(),

                            event.amount()
                    )
            );

            log.info(
                    "Inventory reserved and payment requested: orderId={}, sku={}, quantity={}",
                    event.orderId(),
                    event.sku(),
                    event.quantity()
            );

        } catch (InsufficientStockException e) {

            inventoryProducer.sendInventoryFailedEvent(
                    new InventoryFailedEvent(
                            event.orderId(),
                            event.sku(),
                            event.quantity(),
                            e.getMessage()
                    )
            );

            log.warn(
                    "Inventory reservation failed: orderId={}, sku={}, quantity={}, reason={}",
                    event.orderId(),
                    event.sku(),
                    event.quantity(),
                    e.getMessage()
            );
        }
    }
}