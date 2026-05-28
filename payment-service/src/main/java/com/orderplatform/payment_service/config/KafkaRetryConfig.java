package com.orderplatform.payment_service.config;


import io.micrometer.common.util.internal.logging.InternalLogger;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
public class KafkaRetryConfig {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private InternalLogger log;

    @Bean
    public DefaultErrorHandler errorHandler() {

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> {
                    log.error(
                            "Sending message to DLQ: topic={}, error={}",
                            record.topic(),
                            ex.getMessage()
                    );
                    if(record.topic().equals(KafkaTopics.PAYMENT_REQUESTED)) {
                        return new TopicPartition(KafkaTopics.PAYMENT_REQUESTED_DLQ, record.partition());
                    }
                    return new TopicPartition(
                            record.topic() + "-dlq",
                            record.partition());
                }

        );

        FixedBackOff fixedBackOff = new FixedBackOff(3000L, 3L);

        return new DefaultErrorHandler(recoverer, fixedBackOff);
    }

    @Bean
    public NewTopic paymentRequestedRetryTopic() {

        return TopicBuilder
                .name(KafkaTopics.PAYMENT_REQUESTED_RETRY)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentRequestedDlqTopic() {

        return TopicBuilder
                .name(KafkaTopics.PAYMENT_REQUESTED_DLQ)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
