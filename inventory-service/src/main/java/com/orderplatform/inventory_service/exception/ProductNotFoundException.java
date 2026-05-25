package com.orderplatform.inventory_service.exception;

public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(String sku) {
        super("Product not found by sku: " + sku);
    }
}
