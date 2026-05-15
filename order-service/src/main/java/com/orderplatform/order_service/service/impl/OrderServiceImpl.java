package com.orderplatform.order_service.service.impl;

import com.orderplatform.order_service.dto.CreateOrderRequest;
import com.orderplatform.order_service.dto.OrderResponse;
import com.orderplatform.order_service.dto.UpdateOrderStatusRequest;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderItem;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.exception.InvalidOrderStateException;
import com.orderplatform.order_service.exception.OrderNotFoundException;
import com.orderplatform.order_service.mapper.OrderMapper;
import com.orderplatform.order_service.repository.OrderRepository;
import com.orderplatform.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public OrderResponse createOrder (CreateOrderRequest request) {

        BigDecimal totalAmount = request.items()
                .stream()
                .map(item -> item.price()
                        .multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .customerEmail(request.customerEmail())
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)
                .build();

        List<OrderItem> items = request.items()
                .stream()
                .map(itemRequest -> {

                    OrderItem item = OrderItem.builder()
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

        if (status != null) {

            orders = orderRepository.findALlByStatus(status, pageable);
        } else {

            orders = orderRepository.findAll(pageable);
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
    }
}
