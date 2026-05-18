package com.orderplatform.order_service.kafka;

import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeadLetterConsumer {

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED_DLQ,
            groupId = "dlq-group"
    )
    public void consumeDeadLetter(OrderCreatedEvent event) {
        log.error("Message moved to DLQ: {}",
                event);
    }
}
