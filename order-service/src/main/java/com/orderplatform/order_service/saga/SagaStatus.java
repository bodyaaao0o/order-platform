package com.orderplatform.order_service.saga;

public enum SagaStatus {

    STARTED,

    INVENTORY_RESERVATION_REQUESTED,

    INVENTORY_RESERVED,

    PAYMENT_REQUESTED,

    COMPLETED,

    COMPENSATION_REQUESTED,

    COMPENSATED,

    FAILED,

    NEEDS_MANUAL_REVIEW
}
