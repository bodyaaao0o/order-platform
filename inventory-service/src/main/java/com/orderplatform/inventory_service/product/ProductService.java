package com.orderplatform.inventory_service.product;

import com.orderplatform.inventory_service.product.dto.CreateProductRequest;
import com.orderplatform.inventory_service.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts();

    ProductResponse getBySku(String sku);

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateStock(String sku, Integer stock);

    void deleteProduct(String sku);
}
