package com.orderplatform.email_worker_service.dto;

public record EmailMessage(

        String to,
        String subject,
        String body
) {
}
