package org.loamok.libs.emailtemplates.manager;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loamok.libs.emailtemplates.config.LoamokEmailsTemplatesProperties;
import org.loamok.libs.emailtemplates.entity.EmailDetails;
import org.loamok.libs.emailtemplates.exceptions.EmailSendingException;
import org.loamok.libs.emailtemplates.exceptions.EmailWithAttachmentSendingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Implementation du service d'envoi des e-mails
 * 
 * <p>Ce service utilise les proprietes Spring Boot standard pour la configuration SMTP :
 * <ul>
 *   <li>{@code spring.mail.host} - Serveur SMTP (OBLIGATOIRE)</li>
 *   <li>{@code spring.mail.port} - Port SMTP (OBLIGATOIRE)</li>
 *   <li>{@code spring.mail.username} - Compte SMTP, utilise aussi comme adresse expediteur (OBLIGATOIRE)</li>
 *   <li>{@code spring.mail.password} - Mot de passe SMTP (OBLIGATOIRE)</li>
 * </ul>
 * 
 * <p>Et les proprietes de la bibliotheque pour la configuration metier :
 * <ul>
 *   <li>{@code loamok.security.email.base-url} - URL de base pour les liens dans les emails</li>
 *   <li>{@code loamok.security.email.admin-email} - Email administrateur pour notifications</li>
 * </ul>
 * 
 * <p>Exemple de configuration :
 * <pre>
 * spring:
 *   mail:
 *     host: smtp.example.com
 *     port: 587
 *     username: noreply@example.com
 *     password: secret
 * 
 * loamok:
 *   security:
 *     email:
 *       base-url: http://localhost:8080
 *       admin-email: admin@example.com
 * </pre>
 *
 * @author Huby Franck
 */
@Service
public class EmailManager implements EmailService {

    private final JavaMailSender javaMailSender;
    private final LoamokEmailsTemplatesProperties emailsProperties;
    
    /**
     * Adresse expediteur des emails
     * Recuperee depuis la configuration Spring Boot : {@code spring.mail.username}
     * 
     * <p>Cette propriete est une dependance externe obligatoire.
     * Elle doit correspondre au compte SMTP configure dans {@code spring.mail.*}
     */
    @Value("${spring.mail.username}")
    private String sender;
    
    /**
     * Journalisation
     */
    protected final Log logger = LogFactory.getLog(getClass());
    
    private String baseurl;

    /**
     * Constructeur avec injection des dependances
     * 
     * @param javaMailSender Service Spring Mail pour l'envoi d'emails
     * @param emailsProperties Configuration de la bibliotheque
     */
    @Autowired
    public EmailManager(JavaMailSender javaMailSender, LoamokEmailsTemplatesProperties emailsProperties) {
        this.javaMailSender = javaMailSender;
        this.emailsProperties = emailsProperties;
    }

    @Override
    public String getBaseurl() {
        return baseurl != null ? baseurl : emailsProperties.getEmail().getBaseUrl();
    }
    
    @Override
    public void setBaseurl(String baseurl) {
        this.logger.info("----");
        this.logger.info("setBaseurl");
        this.logger.info("baseurl = " + baseurl);
        this.baseurl = baseurl;
    }
    
    /**
     * Recupere l'email administrateur depuis la configuration
     * 
     * @return Email administrateur defini dans {@code loamok.security.email.admin-email}
     */
    private String getAdminEmail() {
        return emailsProperties.getEmail().getAdminEmail();
    }
    
    @Override
    public String sendSimpleMail(EmailDetails details) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);  // Utilise spring.mail.username
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }
        catch (Exception e) {
            logger.info("sendSimpleMail : ");
            logger.info(e.toString());
            throw new EmailSendingException(details.getRecipient());
        }
    }

    @Override
    public String sendMailWithAttachment(EmailDetails details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);  // Utilise spring.mail.username
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());

            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
            mimeMessageHelper.addAttachment(file.getFilename(), file);

            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }
        catch (MessagingException e) {
            throw new EmailWithAttachmentSendingException(details.getRecipient());
        }
    }
}
