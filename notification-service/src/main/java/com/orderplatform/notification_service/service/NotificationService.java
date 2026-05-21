package com.orderplatform.notification_service.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void sendOrderCreatedEmail(String email, Long orderId)  {
        log.info(
                "Sending email to {} for order {}",
                email,
                orderId
        );
    }
}
