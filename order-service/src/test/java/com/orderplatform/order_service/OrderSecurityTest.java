package com.orderplatform.order_service;


import com.orderplatform.order_service.config.SecurityConfig;
import com.orderplatform.order_service.controller.OrderController;
import com.orderplatform.order_service.security.JwtAuthenticationFilter;
import com.orderplatform.order_service.service.CustomUserDetailsService;
import com.orderplatform.order_service.service.JwtService;
import com.orderplatform.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class OrderSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = {"USER"}
    )
    void shouldReturn403WhenUserDeletesOrder() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "admin@test.com",
            roles = {"ADMIN"}
    )
    void shouldAllowAdminToDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/v1/orders/1").with(csrf())).andExpect(status().isNoContent());
    }
}
