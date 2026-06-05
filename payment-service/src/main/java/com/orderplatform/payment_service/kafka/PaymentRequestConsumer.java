package com.orderplatform.payment_service.kafka;


import com.orderplatform.payment_service.config.KafkaTopics;
import com.orderplatform.payment_service.event.PaymentCompletedEvent;
import com.orderplatform.payment_service.event.PaymentFailedEvent;
import com.orderplatform.payment_service.event.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {

    private final PaymentProducer paymentProducer;

    boolean paymentSuccess = true;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_REQUESTED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(PaymentRequestedEvent event) throws InterruptedException {
        log.info(
                "Received payment request: orderId={}, amount={}",
                event.orderId(),
                event.amount()
        );
        // For testing dead letter queue handling

//        throw new RuntimeException("TEST DLQ");

        Thread.sleep(2000);

        if (paymentSuccess) {
            paymentProducer.sendPaymentCompletedEvent(new PaymentCompletedEvent(event.orderId()));

            log.info(
                    "Payment successful: orderId={}",
                    event.orderId()
            );
        } else {
            paymentProducer.sendPaymentFailedEvent(new PaymentFailedEvent(event.orderId(),
                    "Payment was declined"));

            log.warn(
                    "Payment failed: orderId={}",
                    event.orderId()
            );
        }
    }
}
