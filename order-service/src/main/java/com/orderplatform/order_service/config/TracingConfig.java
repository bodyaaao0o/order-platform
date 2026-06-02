package com.orderplatform.order_service.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {
    /*
     * Spring Boot auto-configures Micrometer Tracer from the tracing bridge
     * dependencies. Keep this class only as a place for future tracing
     * customizations; defining a custom Tracer bean here would override the
     * auto-configured tracer and can break Zipkin export.
     */
}
