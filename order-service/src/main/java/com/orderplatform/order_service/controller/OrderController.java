package com.orderplatform.order_service.controller;


import com.orderplatform.order_service.dto.CreateOrderRequest;
import com.orderplatform.order_service.dto.OrderResponse;
import com.orderplatform.order_service.dto.UpdateOrderStatusRequest;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {

        return orderService.createOrder(request);

    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateOrderStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    @GetMapping
    public Page<OrderResponse> getAllOrders(@RequestParam(required = false)OrderStatus status, Pageable pageable) {
        return orderService.getAllOrders(status, pageable);
    }

}
