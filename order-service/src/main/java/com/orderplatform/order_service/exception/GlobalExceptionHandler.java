package com.orderplatform.order_service.exception;


import com.orderplatform.order_service.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOrderNotFound ( OrderNotFoundException ex) {
        return new ErrorResponse(
                ex.getMessage(),
                404,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidOrderState(
            InvalidOrderStateException ex
    ) {
        return new ErrorResponse(
                ex.getMessage(),
                400,
                LocalDateTime.now()
        );
    }
}

