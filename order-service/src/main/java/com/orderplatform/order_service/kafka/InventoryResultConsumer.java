package com.orderplatform.order_service.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.event.InventoryFailedEvent;
import com.orderplatform.order_service.event.InventoryReservedEvent;
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
public class InventoryResultConsumer {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(
            topics = KafkaTopics.INVENTORY_RESERVED,
            groupId = "order-inventory-result-group-v2",
            containerFactory = "inventoryResultKafkaListenerContainerFactory"
    )
    public void consumeInventoryReserved(String payload) {

        InventoryReservedEvent event =
                readEvent(payload, InventoryReservedEvent.class);

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        if (order.getStatus() == OrderStatus.CREATED) {
            order.setStatus(OrderStatus.PROCESSING);
        }

        log.info(
                "Order inventory reserved: orderId={}, sku={}, quantity={}",
                event.orderId(),
                event.sku(),
                event.quantity()
        );
    }

    @Transactional
    @KafkaListener(
            topics = KafkaTopics.INVENTORY_FAILED,
            groupId = "order-inventory-result-group-v2",
            containerFactory = "inventoryResultKafkaListenerContainerFactory"
    )
    public void consumeInventoryFailed(String payload) {

        InventoryFailedEvent event =
                readEvent(payload, InventoryFailedEvent.class);

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            order.setStatus(OrderStatus.FAILED);
        }

        log.warn(
                "Order failed because inventory reservation failed: orderId={}, sku={}, quantity={}, reason={}",
                event.orderId(),
                event.sku(),
                event.quantity(),
                event.reason()
        );
    }

    private <T> T readEvent(
            String payload,
            Class<T> eventType
    ) {
        try {

            return objectMapper.readValue(
                    payload,
                    eventType
            );

        } catch (JsonProcessingException e) {

            throw new IllegalArgumentException(
                    "Failed to deserialize inventory event payload",
                    e
            );
        }
    }
}
