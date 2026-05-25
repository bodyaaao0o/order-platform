package com.orderplatform.inventory_service.product.mapper;

import com.orderplatform.inventory_service.product.Product;
import com.orderplatform.inventory_service.product.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),

                product.getName(),

                product.getSku(),

                product.getPrice(),

                product.getStock(),

                product.getReservedStock()
        );
    }
}
