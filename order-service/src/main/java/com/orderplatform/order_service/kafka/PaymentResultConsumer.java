package com.orderplatform.order_service.kafka;

import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderItem;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.event.*;
import com.orderplatform.order_service.exception.OrderNotFoundException;
import com.orderplatform.order_service.repository.OrderRepository;
import com.orderplatform.order_service.saga.SagaStateMachine;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final SagaStateMachine sagaStateMachine;

    @Transactional
    @CacheEvict(value = "orders", key = "#event.orderId()")
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_COMPLETED,
            groupId = "payment-result-group-v2",
            containerFactory =
                    "paymentCompletedKafkaListenerContainerFactory"
    )
    public void consumePaymentCompleted(
            PaymentCompletedEvent event
    ) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        sagaStateMachine.markPaymentCompleted(event.orderId());

        if (order.getStatus() == OrderStatus.PROCESSING) {

            order.setStatus(
                    OrderStatus.AWAITING_SHIPMENT
            );

            orderProducer.sendShipmentRequestedEvent(

                    new ShipmentRequestedEvent(

                            order.getId(),

                            order.getCustomerEmail()
                    )
            );

            sagaStateMachine.markShipmentRequested(order.getId());

            log.info(
                    "Shipment requested: orderId={}",
                    order.getId()
            );
        }

        log.info("Order payment completed; awaiting shipment: orderId={}",
                event.orderId());
    }

    @Transactional
    @CacheEvict(value = "orders", key = "#event.orderId()")
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_FAILED,
            groupId = "payment-result-group-v2",
            containerFactory =
                    "paymentFailedKafkaListenerContainerFactory"
    )
    public void consumePaymentFailed(PaymentFailedEvent event) {

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        sagaStateMachine.markPaymentFailed(event.orderId(), event.reason());

        OrderItem item = order.getItems().getFirst();

        if (order.getStatus() != OrderStatus.COMPLETED) {
            order.setStatus(OrderStatus.FAILED);

            orderProducer.sendInventoryReleaseRequestedEvent(
                    new InventoryReleaseRequestedEvent(
                            UUID.randomUUID(),
                            order.getId(),
                            item.getSku(),
                            item.getQuantity()
                    )
            );
        }
        log.warn(
                "Order payment failed: orderId={}, reason={}",
                event.orderId(),
                event.reason()
        );
    }
}
