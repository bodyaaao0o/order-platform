package com.orderplatform.order_service;


import com.orderplatform.order_service.controller.OrderController;
import com.orderplatform.order_service.dto.OrderResponse;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.security.JwtAuthenticationFilter;
import com.orderplatform.order_service.service.CustomUserDetailsService;
import com.orderplatform.order_service.service.JwtService;
import com.orderplatform.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnOrderById() throws Exception{
        OrderResponse response = new OrderResponse(
                1L,
                "test@test.com",
                BigDecimal.valueOf(100),
                OrderStatus.CREATED,
                null,
                null,
                List.of()
        );

        when(orderService.getOrderById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/orders/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerEmail")
                        .value("test@test.com"));

    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        String request = """
            {
              "customerEmail": "invalid-email",
              "items": [
                {
                  "productName": "Laptop",
                  "quantity": 1,
                  "price": 100
                }
              ]
            }
            """;

        mockMvc.perform(
                post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation error"))
                .andExpect(jsonPath("$.errors.customerEmail").exists());

    }
}
