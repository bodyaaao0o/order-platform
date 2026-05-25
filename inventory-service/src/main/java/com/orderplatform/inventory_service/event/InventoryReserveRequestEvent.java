package com.orderplatform.inventory_service.event;

public record InventoryReserveRequestEvent(

        Long orderId,

        String sku,

        Integer quantity
) {
}
