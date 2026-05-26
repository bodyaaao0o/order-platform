package com.orderplatform.payment_service.event;

public record PaymentFailedEvent(

        Long orderId,

        String reason
) {
}
