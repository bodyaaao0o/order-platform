package com.orderplatform.shipping_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
class ShippingServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
