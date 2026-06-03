package com.orderplatform.inventory_service.event;

public record InventoryReleasedEvent(
        Long orderId,
        String sku,
        Integer quantity,
        String releaseId
) {
}
