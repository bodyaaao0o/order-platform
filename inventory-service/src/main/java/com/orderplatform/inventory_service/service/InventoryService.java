package com.orderplatform.inventory_service.service;


import com.orderplatform.inventory_service.product.Product;
import com.orderplatform.inventory_service.exception.InsufficientStockException;
import com.orderplatform.inventory_service.exception.ProductNotFoundException;
import com.orderplatform.inventory_service.product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;

    @Transactional
    public void reserveStock(Long orderId, String sku, Integer quantity) {

        Product product = productRepository.findBySkuForUpdate(sku)
                .orElseThrow(() -> new ProductNotFoundException(sku));

        int availableStock = product.getStock() - product.getReservedStock();

        if (availableStock < quantity) {
            throw new InsufficientStockException(
                    sku, quantity, availableStock
            );
        }

        product.setReservedStock(
                product.getReservedStock() + quantity
        );
    }
}
