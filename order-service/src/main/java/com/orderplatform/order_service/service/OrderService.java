package com.orderplatform.order_service.service;

import com.orderplatform.order_service.dto.CreateOrderRequest;
import com.orderplatform.order_service.dto.OrderResponse;
import com.orderplatform.order_service.dto.UpdateOrderStatusRequest;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(Long id);

    OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request);

    void deleteOrder(Long id);

    Page<OrderResponse> getAllOrders (OrderStatus status, Pageable pageable);
}
