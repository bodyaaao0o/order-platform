package com.orderplatform.order_service.event;

import java.time.LocalDateTime;

public record SagaDeadLetterEvent(
        Long orderId,

        String sourceTopic,

        String eventType,

        String failureReason,

        String rawPayload,

        LocalDateTime failedAt
) {
}
