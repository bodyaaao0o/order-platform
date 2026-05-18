package com.orderplatform.order_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest (

        @Email
        @NotNull
        String email,

        @NotNull
        String password
){
}
