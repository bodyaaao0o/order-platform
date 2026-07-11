package com.orderplatform.order_service.event;

public record ShipmentCreatedEvent(

        Long orderId,

        String trackingNumber
) {
}
