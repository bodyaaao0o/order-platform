package com.orderplatform.order_service.config;

import com.orderplatform.order_service.event.PaymentCompletedEvent;
import com.orderplatform.order_service.event.PaymentFailedEvent;
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
public class PaymentKafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent>
    paymentCompletedKafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            KafkaTraceConsumerInterceptor traceInterceptor
    ) {
        JsonDeserializer<PaymentCompletedEvent> deserializer =
                new JsonDeserializer<>(PaymentCompletedEvent.class, false);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>(
                kafkaProperties.buildConsumerProperties()
        );

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        DefaultKafkaConsumerFactory<String, PaymentCompletedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        props,
                        new StringDeserializer(),
                        deserializer
                );

        ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        @SuppressWarnings("unchecked")
        RecordInterceptor<String, PaymentCompletedEvent> typedInterceptor =
                (RecordInterceptor<String, PaymentCompletedEvent>) (RecordInterceptor<?, ?>) traceInterceptor;
        factory.setRecordInterceptor(typedInterceptor);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent>
    paymentFailedKafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            KafkaTraceConsumerInterceptor traceInterceptor
    ) {
        JsonDeserializer<PaymentFailedEvent> deserializer =
                new JsonDeserializer<>(PaymentFailedEvent.class, false);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>(
                kafkaProperties.buildConsumerProperties()
        );

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        DefaultKafkaConsumerFactory<String, PaymentFailedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        props,
                        new StringDeserializer(),
                        deserializer
                );

        ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        @SuppressWarnings("unchecked")
        RecordInterceptor<String, PaymentFailedEvent> typedInterceptor =
                (RecordInterceptor<String, PaymentFailedEvent>) (RecordInterceptor<?, ?>) traceInterceptor;
        factory.setRecordInterceptor(typedInterceptor);
        return factory;
    }
}