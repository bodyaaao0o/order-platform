package com.orderplatform.order_service.dto;

public record UserResponse (
        Long id,
        String email,
        String role
) {
}
