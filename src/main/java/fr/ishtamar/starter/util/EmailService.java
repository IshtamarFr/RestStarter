package fr.ishtamar.starter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @Value("${spring.mail.username}")
    private String USERNAME;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    final private JavaMailSender emailSender;
    public EmailService(JavaMailSender emailSender){
        this.emailSender=emailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(USERNAME);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
        logger.info("email sent to " + to + " from " + USERNAME);
    }
}
