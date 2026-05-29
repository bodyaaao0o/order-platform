package com.orderplatform.order_service.tracing;

import com.orderplatform.order_service.common.TracingConstants;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaTraceConsumerInterceptor implements RecordInterceptor<Object, Object> {

    @Override
    public ConsumerRecord<Object, Object> intercept(
            ConsumerRecord<Object, Object> record,
            Consumer<Object, Object> consumer
    ) {
        var header = record.headers().lastHeader(TracingConstants.TRACE_ID);
        if (header != null) {
            String traceId = new String(header.value(), StandardCharsets.UTF_8);
            MDC.put(TracingConstants.TRACE_ID, traceId);
        } else {
            MDC.remove(TracingConstants.TRACE_ID);
        }
        return record;
    }

    @Override
    public void afterRecord(
            ConsumerRecord<Object, Object> record,
            Consumer<Object, Object> consumer
    ) {
        MDC.remove(TracingConstants.TRACE_ID);
    }
}