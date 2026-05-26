package com.orderplatform.inventory_service.event;

public record PaymentRequestedEvent (

        Long orderId,

        String customerEmail,

        java.math.BigDecimal amount
){
}
