package com.orderplatform.payment_service.tracing;

import com.orderplatform.payment_service.common.TracingConstants;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class KafkaTraceConsumerInterceptor implements ConsumerInterceptor<String, Object> {

    @Override
    public ConsumerRecords<String, Object> onConsume(ConsumerRecords<String, Object> records) {
        records.forEach((record) -> {
            var header = record.headers()
                    .lastHeader(TracingConstants.TRACE_ID);

            if (header != null) {
                String traceId = new String(
                        header.value(),
                        StandardCharsets.UTF_8
                );

                MDC.put(TracingConstants.TRACE_ID, traceId);
            }
        });

        return records;
    }

    @Override
    public void onCommit(
            Map<TopicPartition, OffsetAndMetadata> offsets
    ) {
    }

    @Override
    public void close() {

        MDC.clear();
    }

    @Override
    public void configure(
            Map<String, ?> configs
    ) {
    }
}
