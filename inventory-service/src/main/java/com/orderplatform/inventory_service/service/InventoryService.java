package com.orderplatform.inventory_service.service;


import com.orderplatform.inventory_service.product.Product;
import com.orderplatform.inventory_service.exception.InsufficientStockException;
import com.orderplatform.inventory_service.exception.ProductNotFoundException;
import com.orderplatform.inventory_service.product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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

    @Transactional
    public void releaseReservedStock(Long orderId, String sku, Integer quantity) {

        Product product = productRepository.findBySkuForUpdate(sku)
                .orElseThrow(() -> new ProductNotFoundException(sku));

        int newReservedStock = product.getReservedStock() - quantity;

        if (newReservedStock < 0) {
            newReservedStock = 0;
        }

        product.setReservedStock(newReservedStock);
    }
}
