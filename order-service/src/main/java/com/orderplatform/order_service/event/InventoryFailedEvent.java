package com.orderplatform.order_service.event;

public record InventoryFailedEvent(
        Long orderId,
        String sku,
        Integer quantity,
        String reason
) {
}
