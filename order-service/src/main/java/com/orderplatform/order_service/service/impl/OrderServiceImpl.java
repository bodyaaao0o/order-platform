package com.orderplatform.order_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderplatform.order_service.dto.CreateOrderRequest;
import com.orderplatform.order_service.dto.OrderResponse;
import com.orderplatform.order_service.dto.UpdateOrderStatusRequest;
import com.orderplatform.order_service.entity.*;
import com.orderplatform.order_service.event.InventoryReserveRequestEvent;
import com.orderplatform.order_service.event.OrderCreatedEvent;
import com.orderplatform.order_service.exception.InvalidOrderStateException;
import com.orderplatform.order_service.exception.OrderNotFoundException;
import com.orderplatform.order_service.mapper.OrderMapper;
import com.orderplatform.order_service.repository.OrderRepository;
import com.orderplatform.order_service.repository.OutboxEventRepository;
import com.orderplatform.order_service.repository.UserRepository;
import com.orderplatform.order_service.service.OrderService;
import com.orderplatform.order_service.config.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public OrderResponse createOrder (CreateOrderRequest request) {

        BigDecimal totalAmount = request.items()
                .stream()
                .map(item -> item.price()
                        .multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Order order = Order.builder()
                .customerEmail(request.customerEmail())
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)
                .user(user)
                .build();

        List<OrderItem> items = request.items()
                .stream()
                .map(itemRequest -> {

                    OrderItem item = OrderItem.builder()
                            .sku(itemRequest.sku())
                            .productName(itemRequest.productName())
                            .quantity(itemRequest.quantity())
                            .price(itemRequest.price())
                            .order(order)
                            .build();

                    return item;
                })
                .toList();

        order.setItems(items);

        Order savedOrder = orderRepository.save(order);

        savedOrder.getItems()
                .stream()
                .map(item -> new InventoryReserveRequestEvent(
                        savedOrder.getId(),
                        item.getSku(),
                        item.getQuantity()
                ))
                .forEach(event -> saveOutboxEvent(
                        KafkaTopics.INVENTORY_RESERVE_REQUESTED,
                        event,
                        "Failed to serialize inventory reserve event"
                ));

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerEmail(),
                savedOrder.getTotalAmount()
        );

        saveOutboxEvent(
                KafkaTopics.ORDER_CREATED,
                orderCreatedEvent,
                "Failed to serialize order event"
        );

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#id")
    public OrderResponse getOrderById (Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    @CacheEvict(value = "orders", key = "#id")
    public OrderResponse updateOrderStatus (Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        validateStatusTransaction( order.getStatus(), request.status());

        order.setStatus(request.status());

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    @CacheEvict(value = "orders", key = "#id")
    public void  deleteOrder (Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders (OrderStatus status, Pageable pageable) {
        Page<Order> orders;

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        if(user.getRole() == UserRole.ADMIN) {
            if (status != null) {

                orders = orderRepository.findALlByStatus(status, pageable);
            } else {

                orders = orderRepository.findAll(pageable);
            }

            return orders.map(orderMapper::toResponse);
        } else {
            if (status != null) {

                orders = orderRepository
                        .findByUserIdAndStatus(
                                user.getId(),
                                status,
                                pageable
                        );

            } else {

                orders = orderRepository
                        .findByUserId(
                                user.getId(),
                                pageable
                        );
            }
        }

        return orders.map(orderMapper::toResponse);

    }

    private void validateStatusTransaction(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.COMPLETED) {
            throw new InvalidOrderStateException("Completed order cannot be modified");
        }

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cancelled order cannot be modified");
        }

        if (currentStatus == OrderStatus.FAILED) {
            throw new InvalidOrderStateException("Failed order cannot be modified");
        }
    }

    private void saveOutboxEvent(
            String eventType,
            Object event,
            String errorMessage
    ) {
        String payload;

        try {

            payload = objectMapper.writeValueAsString(event);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    errorMessage,
                    e
            );
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .eventType(eventType)
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();

        outboxEventRepository.save(outboxEvent);
    }
}
