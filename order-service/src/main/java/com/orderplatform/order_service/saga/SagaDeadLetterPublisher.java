package com.orderplatform.order_service.saga;

import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.event.SagaDeadLetterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SagaDeadLetterPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(SagaDeadLetterEvent event) {
        kafkaTemplate.send(
                KafkaTopics.SAGA_DEAD_LETTER,
                event.orderId().toString(),
                event
        );
    }
}
