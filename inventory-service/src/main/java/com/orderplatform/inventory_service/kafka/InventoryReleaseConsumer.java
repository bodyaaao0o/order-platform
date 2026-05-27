package com.orderplatform.inventory_service.kafka;


import com.orderplatform.inventory_service.config.KafkaTopics;
import com.orderplatform.inventory_service.event.InventoryReleaseRequestedEvent;
import com.orderplatform.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReleaseConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = KafkaTopics.INVENTORY_RELEASE_REQUESTED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(InventoryReleaseRequestedEvent event) {
        inventoryService.releaseReservedStock(
                event.orderId(),
                event.sku(),
                event.quantity()
        );
        log.info(
                "Inventory released: orderId={}, sku={}, quantity={}",
                event.orderId(),
                event.sku(),
                event.quantity()
        );
    }
}
