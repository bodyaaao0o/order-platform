package com.orderplatform.order_service.saga;

import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.event.InventoryReserveRequestEvent;
import com.orderplatform.order_service.event.OrderCreatedEvent;
import com.orderplatform.order_service.kafka.OrderProducer;
import com.orderplatform.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Transactional
    public void startOrderSaga(Order order) {
        if (order.getStatus() == OrderStatus.CREATED) {
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
        }

        orderProducer.sendOrderCreatedEvent(
                new OrderCreatedEvent(
                        order.getId(),
                        order.getCustomerEmail(),
                        order.getTotalAmount()
                )
        );

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

        log.info("Order saga started: orderId={}, orderTopic={}, inventoryTopic={}",
                order.getId(),
                KafkaTopics.ORDER_CREATED,
                KafkaTopics.INVENTORY_RESERVE_REQUESTED);
    }
}
