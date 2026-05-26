package com.orderplatform.inventory_service.product;


import com.orderplatform.inventory_service.product.dto.CreateProductRequest;
import com.orderplatform.inventory_service.product.dto.ProductResponse;
import com.orderplatform.inventory_service.product.dto.UpdateStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(
        name = "Products",
        description = "Inventory product management API"
)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Get all products"
    )
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{sku}")
    @Operation(
            summary = "Get product by SKU"
    )
    public ProductResponse getBySku(@PathVariable String sku) {
        return productService.getBySku(sku);
    }

    @PostMapping
    @Operation(
            summary = "Create new product"
    )
    public ProductResponse createProduct(@RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    @PatchMapping("/{sku}/stock")
    @Operation(
            summary = "Update product stock"
    )
    public ProductResponse updateStock(@PathVariable String sku, @RequestBody UpdateStockRequest request) {
        return productService.updateStock(sku, request.stock());
    }
}
