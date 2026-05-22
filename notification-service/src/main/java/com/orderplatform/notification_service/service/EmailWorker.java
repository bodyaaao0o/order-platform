package com.orderplatform.notification_service.service;


import com.orderplatform.notification_service.config.RabbitConfig;
import com.orderplatform.notification_service.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailWorker {

    @RabbitListener(
            queues = RabbitConfig.EMAIL_QUEUE
    )
    public void processEmail(EmailMessage message) {
        log.info(
                "Sending email to {} with subject {}",
                message.to(),
                message.subject()
        );
    }
}
