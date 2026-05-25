package com.orderplatform.order_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record OrderItemResponse(
        String sku,
        String productName,
        Integer quantity,
        BigDecimal price
) implements Serializable {
}
