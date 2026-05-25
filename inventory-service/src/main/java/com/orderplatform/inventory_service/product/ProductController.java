package com.orderplatform.inventory_service.product;


import com.orderplatform.inventory_service.product.dto.CreateProductRequest;
import com.orderplatform.inventory_service.product.dto.ProductResponse;
import com.orderplatform.inventory_service.product.dto.UpdateStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{sku}")
    public ProductResponse getBySku(@PathVariable String sku) {
        return productService.getBySku(sku);
    }

    @PostMapping
    public ProductResponse createProduct(@RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    @PatchMapping("/{sku}/stock")
    public ProductResponse updateStock(@PathVariable String sku, @RequestBody UpdateStockRequest request) {
        return productService.updateStock(sku, request.stock());
    }
}
