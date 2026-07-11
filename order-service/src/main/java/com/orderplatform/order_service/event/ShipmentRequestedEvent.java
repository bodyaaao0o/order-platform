package com.orderplatform.order_service.event;

public record ShipmentRequestedEvent(

        Long orderId,

        String customerEmail
) {
}
