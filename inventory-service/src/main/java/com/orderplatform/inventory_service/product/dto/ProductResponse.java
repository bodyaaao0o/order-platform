package com.orderplatform.inventory_service.product.dto;

import java.math.BigDecimal;

public record ProductResponse (

        Long id,
        String name,
        String sku,
        BigDecimal price,
        Integer stock,
        Integer reservedStock
){
}
