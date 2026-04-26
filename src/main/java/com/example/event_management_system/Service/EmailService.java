package com.example.event_management_system.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String defaultToAddress;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.from}") String fromAddress,
                        @Value("${spring.mail.default-to}") String defaultToAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.defaultToAddress = defaultToAddress;
    }

    public void sendEmail(String to, String subject, String body) {
        String recipient = (to == null || to.isBlank()) ? defaultToAddress : to;
        if (recipient == null || recipient.isBlank()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
