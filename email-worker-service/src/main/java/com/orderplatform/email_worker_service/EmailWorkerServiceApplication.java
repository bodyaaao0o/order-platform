package com.orderplatform.email_worker_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class EmailWorkerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailWorkerServiceApplication.class, args);
	}

}
