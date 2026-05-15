package com.orderplatform.order_service.dto;

import com.orderplatform.order_service.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateOrderStatusRequest (

        @NotNull
        OrderStatus status,
        LocalDateTime updatedAt
){
}
