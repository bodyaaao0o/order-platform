package com.orderplatform.payment_service.kafka;


import com.orderplatform.payment_service.event.PaymentCompletedEvent;
import com.orderplatform.payment_service.event.PaymentFailedEvent;
import com.orderplatform.payment_service.event.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {

    private final PaymentProducer paymentProducer;

    private final Random random = new Random();

    public void consume(PaymentRequestedEvent event) throws InterruptedException {
        log.info(
                "Received payment request: orderId={}, amount={}",
                event.orderId(),
                event.amount()
        );

        Thread.sleep(2000);

        boolean paymentSuccess = random.nextBoolean();

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
