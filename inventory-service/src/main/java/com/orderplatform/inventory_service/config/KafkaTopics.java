package com.orderplatform.inventory_service.config;

public class KafkaTopics {

    public static final String INVENTORY_RESERVE_REQUESTED =
            "inventory-reserve-requested";

    public static final String INVENTORY_RESERVED =
            "inventory-reserved";

    public static final String INVENTORY_FAILED =
            "inventory-failed";

    public static final String PAYMENT_REQUESTED =
            "payment-requested";

    public static final String INVENTORY_RELEASE_REQUESTED =
            "inventory-release-requested";
}
