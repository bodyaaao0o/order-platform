package com.orderplatform.payment_service.event;

import java.math.BigDecimal;

public record PaymentRequestedEvent (
        Long orderId,

        String customerEmail,

        BigDecimal amount
){
}
