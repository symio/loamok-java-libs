package org.loamok.libs.emailtemplates.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties pour le module de securite Loamok.
 * Relève les paramètres (application[-*].y[a]ml / application.properties)
 *
 * @author Huby Franck
 */
@Data
@ConfigurationProperties(prefix = "loamok.emails")
public class LoamokEmailTemplatesProperties {

    /**
     * Constructeur par défaut
     */
    public LoamokEmailTemplatesProperties() {
        super();
    }

    /**
     * Configuration Email
     */
    private Email email = new Email();

    /**
     * Wrapper de configuration Email
     */
    @Data
    public static class Email {

        /**
         * Constructeur par défaut
         */
        public Email() {
            super();
        }

        /**
         * URL de base pour les liens dans les emails
         */
        private String baseUrl = "http://localhost";

        /**
         * Duree de validite des cles d'activation en heures (defaut: 1h)
         */
        private int keyValidityHours = 1;

        /**
         * Adresse email de l'administrateur pour les notifications
         */
        private String adminEmail = "admin@loamok.org";
    }

}
