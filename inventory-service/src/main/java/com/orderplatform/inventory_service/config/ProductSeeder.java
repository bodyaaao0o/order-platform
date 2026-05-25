package com.orderplatform.inventory_service.config;


import com.orderplatform.inventory_service.entity.Product;
import com.orderplatform.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class ProductSeeder {

    @Bean
    CommandLineRunner seedProducts(
            ProductRepository repository
    ) {

        return args -> {

            if (repository.count() == 0) {

                repository.save(
                        Product.builder()
                                .name("Laptop")
                                .sku("LAPTOP-1")
                                .price(BigDecimal.valueOf(1000))
                                .stock(10)
                                .reservedStock(0)
                                .build()
                );

                repository.save(
                        Product.builder()
                                .name("Phone")
                                .sku("PHONE-1")
                                .price(BigDecimal.valueOf(500))
                                .stock(20)
                                .reservedStock(0)
                                .build()
                );
            }
        };
    }
}
