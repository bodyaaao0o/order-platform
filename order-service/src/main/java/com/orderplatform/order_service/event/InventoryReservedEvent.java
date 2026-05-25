package com.orderplatform.order_service.event;

public record InventoryReservedEvent (
        Long orderId,
        String sku,
        Integer quantity
){
}
