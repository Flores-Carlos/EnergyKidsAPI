package com.gs.energykids.service;

import com.gs.energykids.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public EmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailMessage(String email, String subject, String body) {
        Map<String, String> message = Map.of(
                "email", email,
                "subject", subject,
                "body", body
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, message);
    }
}
