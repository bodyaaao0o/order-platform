package com.orderplatform.notification_service.dto;

public record EmailMessage(

        String to,
        String subject,
        String body
) {
}
