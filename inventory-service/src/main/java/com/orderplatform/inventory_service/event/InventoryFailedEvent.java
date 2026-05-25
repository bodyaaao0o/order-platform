package com.orderplatform.inventory_service.event;

public record InventoryFailedEvent(
        Long orderId,
        String sky,
        Integer quantity,
        String reason
) {
}
