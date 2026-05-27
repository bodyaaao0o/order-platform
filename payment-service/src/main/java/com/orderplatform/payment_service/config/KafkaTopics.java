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
}
