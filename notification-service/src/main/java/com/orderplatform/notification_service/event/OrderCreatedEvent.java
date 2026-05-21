package com.orderplatform.notification_service.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(

        Long orderId,
        String customerEmail,
        BigDecimal totalAmount
){
}
