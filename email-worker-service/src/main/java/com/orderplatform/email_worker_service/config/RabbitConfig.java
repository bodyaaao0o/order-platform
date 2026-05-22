package com.orderplatform.email_worker_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EMAIL_QUEUE =
            "email.queue";

    public static final String EMAIL_EXCHANGE =
            "email.exchange";

    public static final String EMAIL_ROUTING_KEY =
            "email.routingKey";

    @Bean
    public Queue emailQueue() {

        return new Queue(EMAIL_QUEUE);
    }

    @Bean
    public DirectExchange emailExchange() {

        return new DirectExchange(
                EMAIL_EXCHANGE
        );
    }

    @Bean
    public Binding binding(
            Queue emailQueue,
            DirectExchange emailExchange
    ) {

        return BindingBuilder
                .bind(emailQueue)
                .to(emailExchange)
                .with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {

        return new Jackson2JsonMessageConverter();
    }
}
