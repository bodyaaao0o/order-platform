package com.orderplatform.order_service.config;

public class KafkaTopics {

    public static final String ORDER_CREATED = "order-created";

    public static final String ORDER_CREATED_DLQ = "order-created-dlq";

    public static final String INVENTORY_RESERVE_REQUESTED = "inventory-reserve-requested";

    public static final String INVENTORY_RESERVED = "inventory-reserved";

    public static final String INVENTORY_FAILED = "inventory-failed";

    public static final String PAYMENT_COMPLETED = "payment-completed";

    public static final String PAYMENT_FAILED = "payment-failed";

    public static final String PAYMENT_REQUESTED =
            "payment-requested";

    public static final String INVENTORY_RELEASE_REQUESTED =
            "inventory-release-requested";

    public static final String PAYMENT_REQUESTED_RETRY =
            "payment-requested-retry";

    public static final String PAYMENT_REQUESTED_DLQ =
            "payment-requested-dlq";

    public static final String SAGA_DEAD_LETTER =
            "saga-dead-letter";

    public static final String INVENTORY_RELEASED =
            "inventory-released";
}
