package com.orderplatform.order_service.controller;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class TracingDemoController {

    private final Tracer tracer;

    @GetMapping("/trace-demo")
    public String traceDemo() {
        log.info("Starting order tracing demo");

        simulateFindProduct("SKU-12345");
        simulatePayment(99.99);
        simulateDatabaseSave();

        return "Trace demo completed. Check Zipkin for serviceName=order-service";
    }

    private void simulateFindProduct(String sku) {
        runInSpan("inventory.find-product", span -> {
            span.tag("product.sku", sku);
            span.tag("service.target", "inventory-service");
            sleep(100);
            span.tag("product.found", "true");
        });
    }

    private void simulatePayment(double amount) {
        runInSpan("payment.process", span -> {
            span.tag("payment.amount", String.valueOf(amount));
            span.tag("payment.currency", "USD");
            validateCard();
            authorizeTransaction();
            sleep(150);
            span.tag("payment.status", "authorized");
        });
    }

    private void validateCard() {
        runInSpan("payment.validate-card", span -> {
            span.tag("card.type", "VISA");
            sleep(30);
        });
    }

    private void authorizeTransaction() {
        runInSpan("payment.authorize", span -> {
            span.tag("auth.provider", "stripe");
            sleep(50);
        });
    }

    private void simulateDatabaseSave() {
        runInSpan("database.insert-order", span -> {
            span.tag("db.operation", "INSERT");
            span.tag("db.table", "orders");
            sleep(50);
            span.event("row-inserted");
            span.tag("db.rows_affected", "1");
        });
    }

    private void runInSpan(String name, SpanAction action) {
        Span span = tracer.nextSpan().name(name).start();

        try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
            action.run(span);
        } catch (RuntimeException ex) {
            span.tag("error", "true");
            span.tag("error.message", ex.getMessage());
            throw ex;
        } finally {
            span.end();
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Trace demo interrupted", ex);
        }
    }

    @FunctionalInterface
    private interface SpanAction {
        void run(Span span);
    }
}
