package com.orderplatform.order_service.config;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public DefaultErrorHandler errorHandler() {

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, (record, ex) -> new TopicPartition(
                        KafkaTopics.ORDER_CREATED_DLQ,
                        record.partition()
                )
        );

        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3)
        );
    }


}
