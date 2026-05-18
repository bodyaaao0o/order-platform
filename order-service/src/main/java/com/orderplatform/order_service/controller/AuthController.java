package com.orderplatform.order_service.controller;


import com.orderplatform.order_service.dto.AuthResponse;
import com.orderplatform.order_service.dto.LoginRequest;
import com.orderplatform.order_service.dto.RegisterRequest;
import com.orderplatform.order_service.dto.UserResponse;
import com.orderplatform.order_service.entity.User;
import com.orderplatform.order_service.entity.UserRole;
import com.orderplatform.order_service.exception.EmailAlreadyExistsException;
import com.orderplatform.order_service.repository.UserRepository;

import com.orderplatform.order_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest request) {

        if(userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .email(request.email())
                .password(
                        passwordEncoder.encode(request.password())
                )
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(
                request.email()
        ).orElseThrow();

        boolean matches = passwordEncoder.matches(
                request.password(),
                user.getPassword()
        );

        if(!matches) {
            throw new RuntimeException("Incorrect credentials");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        return new UserResponse(
               user.getId(),
               user.getEmail(),
               user.getRole().name()
        );
    }
}
