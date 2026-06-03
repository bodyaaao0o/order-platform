package com.orderplatform.order_service.saga.dto;

import com.orderplatform.order_service.saga.SagaStatus;
import jakarta.validation.constraints.NotNull;

public record ManualResolveRequest(
        @NotNull
        SagaStatus finalStatus,

        String reason
) {
}
