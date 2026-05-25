package com.orderplatform.inventory_service.product.dto;

import java.math.BigDecimal;

public record CreateProductRequest (

        String name,
        String sku,
        BigDecimal price,
        Integer stock
){
}
