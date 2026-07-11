package com.orderplatform.order_service.entity;

public enum OrderStatus {

    CREATED,
    PROCESSING,
    COMPLETED,
    CANCELLED,
    AWAITING_SHIPMENT,
    SHIPPED,
    DELIVERED,
    FAILED
}
