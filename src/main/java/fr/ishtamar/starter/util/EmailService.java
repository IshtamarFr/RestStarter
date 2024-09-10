package fr.ishtamar.starter.util;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}

