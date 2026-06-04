package com.orderplatform.inventory_service.kafka;


import com.orderplatform.inventory_service.config.KafkaTopics;
import com.orderplatform.inventory_service.event.InventoryFailedEvent;
import com.orderplatform.inventory_service.event.InventoryReservedEvent;
import com.orderplatform.inventory_service.event.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendInventoryReservedEvent(InventoryReservedEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, event);
        log.info(
                "INVENTORY_RESERVED SENT: orderId={}",
                event.orderId()
        );
    }

    public void sendInventoryFailedEvent(InventoryFailedEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_FAILED, event);
    }

    public  void sendPaymentRequestedEvent(PaymentRequestedEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_REQUESTED, event);

        InventoryProducer.log.info(
                "Payment requested event sent: orderId={}, amount={}",
                event.orderId(),
                event.amount()
        );
    }
}
