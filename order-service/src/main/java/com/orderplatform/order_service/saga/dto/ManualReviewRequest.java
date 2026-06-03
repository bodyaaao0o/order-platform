package com.orderplatform.order_service.saga.dto;

import jakarta.validation.constraints.NotBlank;

public record ManualReviewRequest(

        @NotBlank
        String reason
) {
}
