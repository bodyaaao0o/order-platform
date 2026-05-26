package com.orderplatform.payment_service.kafka;

import com.orderplatform.payment_service.config.KafkaTopics;
import com.orderplatform.payment_service.event.PaymentCompletedEvent;
import com.orderplatform.payment_service.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentCompletedEvent(PaymentCompletedEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_COMPLETED, event);

        log.info(
                "Payment completed event sent: orderId={}",
                event.orderId()
        );
    }

    public void sendPaymentFailedEvent(PaymentFailedEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, event);

        log.info(
                "Payment failed event sent: orderId={}, reason={}",
                event.orderId(),
                event.reason()
        );
    }


}
