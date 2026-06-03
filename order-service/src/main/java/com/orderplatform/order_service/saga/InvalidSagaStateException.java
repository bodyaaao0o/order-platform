package com.orderplatform.order_service.saga;

public class InvalidSagaStateException extends RuntimeException{

    public InvalidSagaStateException(String message) {
        super(message);
    }
}
