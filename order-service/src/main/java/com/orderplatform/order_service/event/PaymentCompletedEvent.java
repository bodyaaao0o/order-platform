package com.orderplatform.order_service.event;

public record PaymentCompletedEvent(
        Long orderId
) {
}
