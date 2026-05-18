package com.orderplatform.order_service;

import com.orderplatform.order_service.dto.CreateOrderItemRequest;
import com.orderplatform.order_service.dto.CreateOrderRequest;
import com.orderplatform.order_service.dto.OrderResponse;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.mapper.OrderMapper;
import com.orderplatform.order_service.repository.OrderRepository;
import com.orderplatform.order_service.repository.UserRepository;
import com.orderplatform.order_service.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.orderplatform.order_service.entity.User;
import com.orderplatform.order_service.entity.UserRole;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCalculateTotalAmount() {
        CreateOrderRequest request = new CreateOrderRequest(
                "test@test.com",
                List.of(
                        new CreateOrderItemRequest(
                                "Laptop",
                                2,
                                BigDecimal.valueOf(100)
                        )
                )
        );

        Order savedOrder = Order.builder()
                .id(1L)
                .customerEmail("test@test.com")
                .totalAmount(BigDecimal.valueOf(200))
                .status(OrderStatus.CREATED)
                .build();

        OrderResponse response = new OrderResponse(
                1L,
                "test@test.com",
                BigDecimal.valueOf(200),
                OrderStatus.CREATED,
                null,
                null,
                List.of()
        );

        when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder)).thenReturn(response);
        Authentication authentication =
                org.mockito.Mockito.mock(Authentication.class);

        SecurityContext securityContext =
                org.mockito.Mockito.mock(SecurityContext.class);

        when(authentication.getName())
                .thenReturn("test@test.com");

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(java.util.Optional.of(user));
        OrderResponse result = orderService.createOrder(request);
        assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

}
