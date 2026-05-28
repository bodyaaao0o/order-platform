package com.orderplatform.inventory_service.tracing;

import com.orderplatform.inventory_service.common.TracingConstants;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class KafkaTraceProducerInterceptor implements ProducerInterceptor<String, Object> {

    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> record) {
        String traceId = MDC.get(TracingConstants.TRACE_ID);

        if (traceId != null) {
            record.headers().add(
                    TracingConstants.TRACE_ID,
                    traceId.getBytes(StandardCharsets.UTF_8)
            );
        }

        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata,  Exception exception) {}

    @Override
    public void close(){}

    @Override
    public void configure(Map<String, ?> configs){}
}
