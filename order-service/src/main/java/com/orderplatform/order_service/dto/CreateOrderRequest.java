package com.orderplatform.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateOrderRequest (
        @Email
        @NotBlank
        String customerEmail,

        @Valid
        @NotEmpty
        List<CreateOrderItemRequest> items
) {
}
