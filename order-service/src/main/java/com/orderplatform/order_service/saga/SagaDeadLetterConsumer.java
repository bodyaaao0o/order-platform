package com.orderplatform.order_service.saga;

import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.event.SagaDeadLetterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaDeadLetterConsumer {
    private final SagaStateMachine sagaStateMachine;

    @Transactional
    @KafkaListener(
            topics = KafkaTopics.SAGA_DEAD_LETTER,
            groupId = "saga-dead-letter-group"
    )
    public void consume(SagaDeadLetterEvent event) {
        String reason = "DLQ event received. sourceTopic="
                + event.sourceTopic()
                + ", eventType="
                + event.eventType()
                + ", reason="
                + event.failureReason();

        sagaStateMachine.markManualReview(
                event.orderId(),
                reason
        );

        log.warn(
                "Saga moved to manual review from DLQ: orderId={}, sourceTopic={}, eventType={}, reason={}",
                event.orderId(),
                event.sourceTopic(),
                event.eventType(),
                event.failureReason()
        );
    }
}
