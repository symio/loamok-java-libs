package org.loamok.libs.emailtemplates;

import org.loamok.libs.emailtemplates.config.LoamokEmailTemplatesProperties;
import org.loamok.libs.emailtemplates.manager.EmailManager;
import org.loamok.libs.emailtemplates.manager.EmailService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Configuration automatique pour le module de templates e-mails Loamok.
 * Wrapper fournissant des beans
 * 
 * @author Huby Franck
 */
@AutoConfiguration
@ComponentScan(
    basePackages = {
        "org.loamok.libs.emailtemplates.dto.email",
        "org.loamok.libs.emailtemplates.manager"
    }
)
public class EmailTemplatesAutoConfiguration {

    /**
     * Email manager
     * 
     * @param javaMailSender Service Spring Mail pour l'envoi d'emails
     * @param emailsProperties Configuration de la bibliotheque
     * @return EmailService
     */
    @Bean
    @ConditionalOnMissingBean
    public EmailService emailService(JavaMailSender javaMailSender, LoamokEmailTemplatesProperties emailsProperties) {
        return new EmailManager(javaMailSender, emailsProperties);
    }

}
