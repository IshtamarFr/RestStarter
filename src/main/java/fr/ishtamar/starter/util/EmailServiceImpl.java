package fr.ishtamar.starter.util;

import fr.ishtamar.starter.user.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("!dev")
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String USERNAME;
    @Value("${fr.ishtamar.starter.prod-url}")
    private String BASE_URL;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender emailService;
    private final UserInfoService userInfoService;

    public EmailServiceImpl(JavaMailSender emailService, UserInfoService userInfoService){
        this.emailService = emailService;
        this.userInfoService=userInfoService;
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(USERNAME);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailService.send(message);
        logger.info("email sent to " + to + " from " + USERNAME);
    }

    @Override
    public void sendValidationLink(String to, Long id, String token) {
        sendSimpleMessage(to, "Inscription : Starter",
                "Bienvenue sur le Starter. Pour valider votre inscription, merci de cliquer sur ce lien : "
                        + BASE_URL+ "/#/validate?id=" + id + "&token=" + token
        );
    }

    @Override
    public void sendTemporaryPassword(String to, String token) {
        try {
            userInfoService.getUserByUsername(to); //required to see if this user exists before trying to send en email
            sendSimpleMessage(to, "Mot de passe oublié : Starter",
                    "Bonjour, voici un mot de passe temporaire pour vous connecter sur le Starter : "
                            + BASE_URL + " : " + token
                            + "\n\nAttention, il n'est valable que pour une seule connexion. Pensez à modifier votre mot de passe."
            );
        }catch(Error e){
            log.info("someone required temp password for {} but they don't exist", to);
        }
    }
}
