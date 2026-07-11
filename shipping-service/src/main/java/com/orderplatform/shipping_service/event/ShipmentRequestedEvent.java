package com.orderplatform.shipping_service.event;

public record ShipmentRequestedEvent(

        Long orderId,

        String customerEmail
) {
}
