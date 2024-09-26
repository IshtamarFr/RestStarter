package fr.ishtamar.starter.util;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
    void sendValidationLink(String to,Long id,String token);
    void sendTemporaryPassword(String to,String token);
}

