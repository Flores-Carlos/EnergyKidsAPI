package com.gs.energykids.service;

import com.gs.energykids.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailMessage(Map<String, String> message) {
        System.out.printf("Mensagem consumida: %s%n", message);

        String email = message.get("email");
        String subject = message.get("subject");
        String body = message.get("body");

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            mailSender.send(mailMessage);

            System.out.printf("E-mail enviado para %s com assunto '%s'.%n", email, subject);
        } catch (Exception e) {
            System.err.printf("Erro ao enviar e-mail para %s: %s%n", email, e.getMessage());
        }
    }
}
