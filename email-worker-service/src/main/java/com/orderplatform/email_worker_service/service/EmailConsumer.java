package com.orderplatform.email_worker_service.service;


import com.orderplatform.email_worker_service.config.RabbitConfig;
import com.orderplatform.email_worker_service.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailConsumer {

    @RabbitListener(
            queues = RabbitConfig.EMAIL_QUEUE
    )
    public void precessEmail(EmailMessage message) throws InterruptedException {

        log.info(
                "Processing email: {}",
                message
        );

        Thread.sleep(2000);

        log.info(
                "Email sent to {}",
                message.to()
        );

    }
}
