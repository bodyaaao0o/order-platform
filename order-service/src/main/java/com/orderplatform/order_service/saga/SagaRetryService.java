package com.orderplatform.order_service.saga;

import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderItem;
import com.orderplatform.order_service.event.InventoryReleaseRequestedEvent;
import com.orderplatform.order_service.event.InventoryReserveRequestEvent;
import com.orderplatform.order_service.event.PaymentRequestedEvent;
import com.orderplatform.order_service.exception.OrderNotFoundException;
import com.orderplatform.order_service.kafka.OrderProducer;
import com.orderplatform.order_service.repository.OrderRepository;
import com.orderplatform.order_service.event.SagaDeadLetterEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaRetryService {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStateMachine sagaStateMachine;
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final SagaDeadLetterPublisher sagaDeadLetterPublisher;

    @Transactional
    public SagaInstance retry(Long sagaId) {
        SagaInstance saga = sagaInstanceRepository.findById(sagaId)
                .orElseThrow(() -> new InvalidSagaStateException(
                        "Saga not found: id=" + sagaId
                ));

        if (!saga.canRetry()) {
            sagaDeadLetterPublisher.publish(
                    new SagaDeadLetterEvent(
                            saga.getOrderId(),
                            resolveSourceTopic(saga.getStatus()),
                            saga.getLastEventType(),
                            "Saga retry limit reached",
                            null,
                            LocalDateTime.now()
                    )
            );

            return sagaStateMachine.markManualReview(
                    saga.getOrderId(),
                    "Saga retry limit reached"
            );
        }

        sagaStateMachine.incrementRetry(saga.getOrderId());

        switch (saga.getStatus()) {
            case INVENTORY_RESERVATION_REQUESTED ->
                    retryInventoryReservation(saga);

            case PAYMENT_REQUESTED ->
                    retryPaymentRequest(saga);

            case COMPENSATION_REQUESTED ->
                    retryInventoryCompensation(saga);

            case NEEDS_MANUAL_REVIEW ->
                    throw new InvalidSagaStateException(
                            "Saga is in manual review and cannot be retried automatically"
                    );

            default ->
                    throw new InvalidSagaStateException(
                            "Saga status cannot be retried: " + saga.getStatus()
                    );
        }

        log.info(
                "Saga retry published: sagaId={}, orderId={}, status={}, retryCount={}",
                saga.getId(),
                saga.getOrderId(),
                saga.getStatus(),
                saga.getRetryCount()
        );

        return saga;
    }

    private void retryInventoryReservation(SagaInstance saga) {
        Order order = getOrder(saga.getOrderId());

        order.getItems().forEach(item -> orderProducer.sendInventoryReserveRequestEvent(
                new InventoryReserveRequestEvent(
                        UUID.randomUUID(),
                        order.getId(),
                        item.getSku(),
                        item.getQuantity(),
                        order.getCustomerEmail(),
                        order.getTotalAmount()
                )
        ));
    }

    private void retryPaymentRequest(SagaInstance saga) {
        Order order = getOrder(saga.getOrderId());

        orderProducer.sendPaymentRequestedEvent(
                new PaymentRequestedEvent(
                        order.getId(),
                        order.getTotalAmount(),
                        order.getCustomerEmail()
                )
        );
    }

    private void retryInventoryCompensation(SagaInstance saga) {
        Order order = getOrder(saga.getOrderId());

        OrderItem item = order.getItems().getFirst();

        orderProducer.sendInventoryReleaseRequestedEvent(
                new InventoryReleaseRequestedEvent(
                        UUID.randomUUID(),
                        order.getId(),
                        item.getSku(),
                        item.getQuantity()
                )
        );
    }

    private String resolveSourceTopic(SagaStatus status) {
        return switch (status) {
            case INVENTORY_RESERVATION_REQUESTED ->
                    KafkaTopics.INVENTORY_RESERVE_REQUESTED;

            case PAYMENT_REQUESTED ->
                    KafkaTopics.PAYMENT_REQUESTED;

            case COMPENSATION_REQUESTED ->
                    KafkaTopics.INVENTORY_RELEASE_REQUESTED;

            default ->
                    "unknown";
        };
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
