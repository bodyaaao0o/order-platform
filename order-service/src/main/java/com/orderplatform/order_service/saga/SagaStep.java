package com.orderplatform.order_service.saga;

public enum SagaStep {

    ORDER_CREATED,

    INVENTORY_RESERVATION,

    PAYMENT_PROCESSING,

    INVENTORY_COMPENSATION,

    FINISHED
}
