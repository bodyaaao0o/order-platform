package com.orderplatform.order_service.dto;

import com.orderplatform.order_service.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest (

        @NotNull
        OrderStatus status
){
}
