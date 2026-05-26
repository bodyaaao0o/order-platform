package com.orderplatform.order_service.event;

public record PaymentFailedEvent(
        Long orderId,

        String reason
) {
}
