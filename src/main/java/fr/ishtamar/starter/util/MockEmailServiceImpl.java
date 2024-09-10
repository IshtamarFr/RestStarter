package fr.ishtamar.starter.util;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class MockEmailServiceImpl implements EmailService{
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        System.out.println("Mock email sent to " + to + " : " + text);
    }
}
