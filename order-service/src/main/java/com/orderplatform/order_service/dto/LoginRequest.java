package com.orderplatform.order_service.dto;

public record LoginRequest(

        String email,
        String password
) {
}
