package com.orderplatform.order_service.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCreatedTopic() {

        return new NewTopic(
                KafkaTopics.ORDER_CREATED,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic orderCreatedDlqTopic() {

        return new NewTopic(
                KafkaTopics.ORDER_CREATED_DLQ,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic inventoryReserveRequestedTopic() {

        return new NewTopic(
                KafkaTopics.INVENTORY_RESERVE_REQUESTED,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic inventoryReservedTopic() {
        return new NewTopic(
                KafkaTopics.INVENTORY_RESERVED,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic inventoryReleasedTopic() {
        return new NewTopic(
                KafkaTopics.INVENTORY_RELEASED,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic inventoryFailedTopic() {
        return new NewTopic(
                KafkaTopics.INVENTORY_FAILED,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic sagaDeadLetterTopic() {
        return new NewTopic(
                KafkaTopics.SAGA_DEAD_LETTER,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic shipmentRequestedTopic() {
        return new NewTopic(
                KafkaTopics.SHIPMENT_REQUESTED,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic shipmentCreatedTopic() {
        return new NewTopic(
                KafkaTopics.SHIPMENT_CREATED,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic shipmentDeliveredTopic() {
        return new NewTopic(
                KafkaTopics.SHIPMENT_DELIVERED,
                1,
                (short) 1
        );
    }

}
