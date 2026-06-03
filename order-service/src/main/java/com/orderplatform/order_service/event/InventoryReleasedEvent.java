package com.orderplatform.order_service.event;

public record InventoryReleasedEvent(
        Long orderId,

        String sku,

        Integer quantity,

        String releaseId
) {
}
