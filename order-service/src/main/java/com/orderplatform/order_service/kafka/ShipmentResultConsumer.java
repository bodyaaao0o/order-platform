package com.orderplatform.order_service.kafka;

import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.event.ShipmentCreatedEvent;
import com.orderplatform.order_service.event.ShipmentDeliveredEvent;
import com.orderplatform.order_service.exception.OrderNotFoundException;
import com.orderplatform.order_service.repository.OrderRepository;
import com.orderplatform.order_service.saga.SagaStateMachine;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentResultConsumer {

    private final OrderRepository orderRepository;
    private final SagaStateMachine sagaStateMachine;

    @Transactional
    @CacheEvict(value = "orders", key = "#event.orderId()")
    @KafkaListener(
            topics = KafkaTopics.SHIPMENT_CREATED,
            groupId = "shipment-result-group-v1",
            containerFactory =
                    "shipmentCreatedKafkaListenerContainerFactory"
    )
    public void consumeShipmentCreated(ShipmentCreatedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        sagaStateMachine.markShipmentCreated(event.orderId());

        if (order.getStatus() == OrderStatus.AWAITING_SHIPMENT) {
            order.setStatus(OrderStatus.SHIPPED);
        }

        log.info(
                "Shipment created: orderId={}, trackingNumber={}",
                event.orderId(),
                event.trackingNumber()
        );
    }

    @Transactional
    @CacheEvict(value = "orders", key = "#event.orderId()")
    @KafkaListener(
            topics = KafkaTopics.SHIPMENT_DELIVERED,
            groupId = "shipment-result-group-v1",
            containerFactory =
                    "shipmentDeliveredKafkaListenerContainerFactory"
    )
    public void consumeShipmentDelivered(ShipmentDeliveredEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        sagaStateMachine.markShipmentDelivered(event.orderId());

        if (order.getStatus() == OrderStatus.SHIPPED) {
            order.setStatus(OrderStatus.DELIVERED);
        }

        log.info("Shipment delivered: orderId={}", event.orderId());
    }
}
