package com.orderplatform.payment_service.event;

public record PaymentRequestedEvent (
        Long orderId,

        String customerEmail,

        Double amount
){
}
