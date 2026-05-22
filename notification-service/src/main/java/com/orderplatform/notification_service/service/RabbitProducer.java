package com.orderplatform.notification_service.service;


import com.orderplatform.notification_service.config.RabbitConfig;
import com.orderplatform.notification_service.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEmailMessage(EmailMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EMAIL_EXCHANGE,
                RabbitConfig.EMAIL_ROUTING_KEY,
                message
        );

        log.info("Email message send to queue: {}", message);
    }
}
