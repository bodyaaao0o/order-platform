package com.orderplatform.inventory_service.exception;

public class InsufficientStockException extends RuntimeException{

    public InsufficientStockException(String sku, Integer requested, Integer available) {
        super(
                "Insufficient stock for sku " + sku +
                        ". Requested: " + requested +
                        ", available: " + available
        );
    }
}
