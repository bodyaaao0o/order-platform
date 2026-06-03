package com.orderplatform.order_service.saga;

import com.orderplatform.order_service.saga.dto.SagaInstanceResponse;
import org.springframework.stereotype.Component;

@Component
public class SagaInstanceMapper {

    public SagaInstanceResponse toResponse(SagaInstance saga) {
        return new SagaInstanceResponse(
                saga.getId(),
                saga.getOrderId(),
                saga.getStatus(),
                saga.getCurrentStep(),
                saga.getFailureReason(),
                saga.getRetryCount(),
                saga.getMaxRetries(),
                saga.getLastEventType(),
                saga.getLastEventAt(),
                saga.getCreatedAt(),
                saga.getUpdatedAt(),
                saga.getCompletedAt()
        );
    }
}
