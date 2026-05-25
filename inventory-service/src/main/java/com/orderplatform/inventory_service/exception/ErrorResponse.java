package com.orderplatform.inventory_service.exception;

import java.time.LocalDateTime;

public record ErrorResponse (
        LocalDateTime timestamp,

        Integer status,

        String error,

        String path
){
}
