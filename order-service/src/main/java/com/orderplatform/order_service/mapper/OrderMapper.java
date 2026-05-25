package com.orderplatform.order_service.mapper;


import com.orderplatform.order_service.dto.OrderItemResponse;
import com.orderplatform.order_service.dto.OrderResponse;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items

        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {

        return new OrderItemResponse(
                item.getSku(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
