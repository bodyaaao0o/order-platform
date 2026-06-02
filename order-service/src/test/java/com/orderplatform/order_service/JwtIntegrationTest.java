package com.orderplatform.order_service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderplatform.order_service.entity.User;
import com.orderplatform.order_service.entity.UserRole;
import com.orderplatform.order_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User user = User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    @Test
    void shouldAuthenticateWithJwt() throws Exception {
        String loginRequest = """
                {
                  "email": "test@test.com",
                  "password": "password"
                }
                """;

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response)
                .get("token")
                .asText();

        mockMvc.perform(get("/api/v1/orders").header(
                "Authorization",
                "Bearer " + token
        ))
                .andExpect(status().isOk());


    }
}
