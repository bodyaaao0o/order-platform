package com.orderplatform.order_service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.orderplatform.order_service.config.KafkaTopics;
import com.orderplatform.order_service.dto.CreateOrderItemRequest;
import com.orderplatform.order_service.dto.CreateOrderRequest;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.entity.User;
import com.orderplatform.order_service.entity.UserRole;

import com.orderplatform.order_service.repository.OrderRepository;
import com.orderplatform.order_service.repository.UserRepository;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.apache.kafka.common.serialization.StringDeserializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setup() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafka.getBootstrapServers()
        );

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "test-group"
        );

        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        consumer = new DefaultKafkaConsumerFactory<String, String>(
                props
        ).createConsumer();

        consumer.subscribe(
                List.of(KafkaTopics.ORDER_CREATED)
        );
    }

    @Test
    @WithMockUser(
            username = "testuser",
            roles = {"USER"}
    )
    void shouldCreateOrder() throws Exception {

        User user = User.builder()
                .email("testuser")
                .password("password")
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        CreateOrderRequest request =
                new CreateOrderRequest(
                        "test@test.com",
                        List.of(
                                new CreateOrderItemRequest(
                                        "LAPTOP-1",
                                        "Laptop",
                                        1,
                                        BigDecimal.valueOf(1000)
                                )
                        )
                );

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk());

        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(
                        consumer,
                        KafkaTopics.ORDER_CREATED
                );

        assertThat(record.value())
                .contains("test@test.com");

        Order savedOrder = orderRepository.findAll().getFirst();

        assertThat(savedOrder.getCustomerEmail())
                .isEqualTo("test@test.com");

        assertThat(savedOrder.getStatus())
                .isEqualTo(OrderStatus.CREATED);

        assertThat(savedOrder.getTotalAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(1000));
    }
}
