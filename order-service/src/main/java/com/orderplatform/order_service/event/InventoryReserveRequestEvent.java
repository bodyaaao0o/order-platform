package com.orderplatform.order_service.event;

public record InventoryReserveRequestEvent(
        Long orderId,

        String sku,

        Integer quantity
){
}
