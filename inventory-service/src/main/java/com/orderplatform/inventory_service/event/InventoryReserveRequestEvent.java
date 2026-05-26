package com.orderplatform.inventory_service.event;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryReserveRequestEvent(

        UUID eventId,

        Long orderId,

        String sku,

        Integer quantity,

        String customerEmail,

        BigDecimal amount
) {
}
