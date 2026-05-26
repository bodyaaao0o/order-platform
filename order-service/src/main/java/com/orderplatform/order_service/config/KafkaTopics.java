package com.orderplatform.order_service.config;

public class KafkaTopics {

    public static final String ORDER_CREATED = "order-created";

    public static final String ORDER_CREATED_DLQ = "order-created-dlq";

    public static final String INVENTORY_RESERVE_REQUESTED = "inventory-reserve-requested";

    public static final String INVENTORY_RESERVED = "inventory-reserved";

    public static final String INVENTORY_FAILED = "inventory-failed";

    public static final String PAYMENT_COMPLETED = "payment-completed";

    public static final String PAYMENT_FAILED = "payment-failed";
}
