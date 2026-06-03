package com.orderplatform.order_service.saga.dto;

import com.orderplatform.order_service.saga.SagaStatus;
import com.orderplatform.order_service.saga.SagaStep;

import java.time.LocalDateTime;

public record SagaInstanceResponse(
        Long id,
        Long orderId,
        SagaStatus status,
        SagaStep currentStep,
        String failureReason,
        int retryCount,
        int maxRetries,
        String lastEventType,
        LocalDateTime lastEventAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt
) {
}
