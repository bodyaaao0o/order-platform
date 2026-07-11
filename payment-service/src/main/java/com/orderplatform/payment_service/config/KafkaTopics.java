package com.orderplatform.payment_service.config;

public class KafkaTopics {

    public static final String PAYMENT_REQUESTED =
            "payment-requested";

    public static final String PAYMENT_COMPLETED =
            "payment-completed";

    public static final String PAYMENT_FAILED =
            "payment-failed";

    public static final String INVENTORY_RELEASE_REQUESTED =
            "inventory-release-requested";

    public static final String PAYMENT_REQUESTED_RETRY =
            "payment-requested-retry";

    public static final String PAYMENT_REQUESTED_DLQ =
            "payment-requested-dlq";

    public static final String SHIPMENT_REQUESTED = "shipment-requested";

    public static final String SHIPMENT_CREATED = "shipment-created";

    public static final String SHIPMENT_DELIVERED = "shipment-delivered";
}
