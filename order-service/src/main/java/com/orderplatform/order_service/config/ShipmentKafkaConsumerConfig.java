package com.orderplatform.order_service.config;

import com.orderplatform.order_service.event.ShipmentCreatedEvent;
import com.orderplatform.order_service.event.ShipmentDeliveredEvent;
import com.orderplatform.order_service.tracing.KafkaTraceConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShipmentKafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ShipmentCreatedEvent>
    shipmentCreatedKafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            KafkaTraceConsumerInterceptor traceInterceptor
    ) {
        JsonDeserializer<ShipmentCreatedEvent> deserializer =
                new JsonDeserializer<>(ShipmentCreatedEvent.class, false);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>(
                kafkaProperties.buildConsumerProperties()
        );
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        DefaultKafkaConsumerFactory<String, ShipmentCreatedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        props,
                        new StringDeserializer(),
                        deserializer
                );

        ConcurrentKafkaListenerContainerFactory<String, ShipmentCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        @SuppressWarnings("unchecked")
        RecordInterceptor<String, ShipmentCreatedEvent> typedInterceptor =
                (RecordInterceptor<String, ShipmentCreatedEvent>) (RecordInterceptor<?, ?>) traceInterceptor;
        factory.setRecordInterceptor(typedInterceptor);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ShipmentDeliveredEvent>
    shipmentDeliveredKafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            KafkaTraceConsumerInterceptor traceInterceptor
    ) {
        JsonDeserializer<ShipmentDeliveredEvent> deserializer =
                new JsonDeserializer<>(ShipmentDeliveredEvent.class, false);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>(
                kafkaProperties.buildConsumerProperties()
        );
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        DefaultKafkaConsumerFactory<String, ShipmentDeliveredEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        props,
                        new StringDeserializer(),
                        deserializer
                );

        ConcurrentKafkaListenerContainerFactory<String, ShipmentDeliveredEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        @SuppressWarnings("unchecked")
        RecordInterceptor<String, ShipmentDeliveredEvent> typedInterceptor =
                (RecordInterceptor<String, ShipmentDeliveredEvent>) (RecordInterceptor<?, ?>) traceInterceptor;
        factory.setRecordInterceptor(typedInterceptor);

        return factory;
    }
}
