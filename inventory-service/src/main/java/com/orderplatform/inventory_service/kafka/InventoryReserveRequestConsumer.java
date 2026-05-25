package com.orderplatform.inventory_service.kafka;

import com.orderplatform.inventory_service.config.KafkaTopics;
import com.orderplatform.inventory_service.event.InventoryReserveRequestEvent;
import com.orderplatform.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReserveRequestConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = KafkaTopics.INVENTORY_RESERVE_REQUESTED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(InventoryReserveRequestEvent event) {

        log.info(
                "Received inventory reserve request: orderId={}, sku={}, quantity={}",
                event.orderId(),
                event.sku(),
                event.quantity()
        );

        inventoryService.reserveStock(
                event.orderId(),
                event.sku(),
                event.quantity()
        );

        log.info(
                "Inventory reserved: orderId={}, sku={}, quantity={}",
                event.orderId(),
                event.sku(),
                event.quantity()
        );
    }
}
