package fr.ishtamar.starter.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class MockEmailServiceImpl implements EmailService{
    @Value("${fr.ishtamar.starter.dev-url}")
    private String BASE_URL;

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        System.out.println("Mock email sent to " + to + " : " + text);
    }

    @Override
    public void sendValidationLink(String to, Long id, String token) {
        sendSimpleMessage(to, "Inscription : Starter",
                "Lien pour valider l'inscription (dev) : "
                        + BASE_URL+ "/#/validate?id=" + id + "&token=" + token
        );
    }

    @Override
    public void sendTemporaryPassword(String to, String token) {
        sendSimpleMessage(to,"MdP temporaire : Starter",
                "Mot de passe temporaire (dev) : " + token);
    }
}
