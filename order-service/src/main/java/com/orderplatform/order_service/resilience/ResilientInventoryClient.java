package com.orderplatform.order_service.resilience;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ResilientInventoryClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @TimeLimiter(name = "inventory-service")
    @CircuitBreaker(
            name = "inventory-service",
            fallbackMethod = "inventoryServiceAsyncFallback"
    )
    @Retry(name = "inventory-service")
    public CompletableFuture<String> checkInventoryAsync(String productSku) {
        return CompletableFuture.supplyAsync(() -> checkInventoryDirect(productSku));
    }

    @CircuitBreaker(
            name = "inventory-service",
            fallbackMethod = "inventoryServiceFallback"
    )
    @Retry(name = "inventory-service")
    public String checkInventory(String productSku) {
        return checkInventoryDirect(productSku);
    }

    public CompletableFuture<String> inventoryServiceAsyncFallback(
            String productSku,
            Throwable ex
    ) {
        return CompletableFuture.completedFuture(inventoryServiceFallback(productSku, ex));
    }

    public String inventoryServiceFallback(String productSku, Throwable ex) {
        log.warn(
                "Inventory fallback activated: sku={}, reason={}",
                productSku,
                ex.getMessage()
        );

        return "Inventory service is temporarily unavailable. Try again later.";
    }

    public void demonstrateCircuitBreaker() {
        log.info("Circuit breaker demo started");

        for (int i = 0; i < 6; i++) {
            try {
                log.info("Inventory demo result: {}", checkInventory("SKU-001"));
            } catch (RuntimeException ex) {
                log.warn("Inventory demo request failed: {}", ex.getMessage());
            }
        }
    }

    private String checkInventoryDirect(String productSku) {
        log.info("Requesting inventory data: sku={}", productSku);

        if (Math.random() < 0.3) {
            throw new IllegalStateException("Inventory service did not respond");
        }

        // Placeholder for a real call:
        // return restTemplate.getForObject("http://inventory-service:8084/api/inventory/" + productSku, String.class);
        return "Product is available: " + productSku;
    }
}
