package com.orderplatform.order_service.dto;

import com.orderplatform.order_service.entity.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse (

        Long id,
        String customerEmail,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt, List<OrderItemResponse> items

) implements Serializable {
}
