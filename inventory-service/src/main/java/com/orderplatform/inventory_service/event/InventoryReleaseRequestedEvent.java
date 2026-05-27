package com.orderplatform.inventory_service.event;

import java.util.UUID;

public record InventoryReleaseRequestedEvent(

        UUID eventId,

        Long orderId,

        String sku,

        Integer quantity
) {
}
