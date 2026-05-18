package com.orderplatform.order_service.event;

import java.math.BigDecimal;

public record OrderCreatedEvent (

        Long orderId,
        String customerEmail,
        BigDecimal totalAmount
){
}
