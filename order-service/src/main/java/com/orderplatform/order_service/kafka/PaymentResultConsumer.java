package com.orderplatform.order_service.kafka;

import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.event.PaymentCompletedEvent;
import com.orderplatform.order_service.event.PaymentFailedEvent;
import com.orderplatform.order_service.exception.OrderNotFoundException;
import com.orderplatform.order_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final OrderRepository orderRepository;

    @Transactional
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_COMPLETED,
            groupId = "payment-result-group"
    )
    public void consumePaymentCompleted(
            PaymentCompletedEvent event
    ) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        order.setStatus(OrderStatus.COMPLETED);

        log.info("Order completed successfully: orderId={}",
                event.orderId());
    }

    @Transactional
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_FAILED,
            groupId = "payment-result-group"
    )
    public void consumePaymentFailed(PaymentFailedEvent event) {

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        order.setStatus(OrderStatus.FAILED);
        log.warn(
                "Order payment failed: orderId={}, reason={}",
                event.orderId(),
                event.reason()
        );
    }
}
