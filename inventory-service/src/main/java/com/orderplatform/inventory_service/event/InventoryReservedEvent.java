package com.orderplatform.inventory_service.event;

public record InventoryReservedEvent(
        Long orderId,
        String sku,
        Integer quantity
) {
}
