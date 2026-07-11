package com.orderplatform.shipping_service.event;

public record ShipmentCreatedEvent(

        Long orderId,

        String trackingNumber
) {
}
