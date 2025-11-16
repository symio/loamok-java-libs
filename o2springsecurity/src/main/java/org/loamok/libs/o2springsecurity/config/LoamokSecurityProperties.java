package org.loamok.libs.o2springsecurity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties pour le module de securite Loamok.
 * Relève les paramètres (application[-*].y[a]ml / application.properties)
 *
 * @author Huby Franck
 */
@Data
@ConfigurationProperties(prefix = "loamok.security")
public class LoamokSecurityProperties {

    /**
     * Constructeur par défaut
     */
    public LoamokSecurityProperties() {
        super();
    }

    /**
     * Configuration JWT
     */
    private Jwt jwt = new Jwt();

    /**
     * Configuration CORS
     */
    private Cors cors = new Cors();

    /**
     * Configuration Email
     */
    private Email email = new Email();

    /**
     * Configuration des endpoints
     */
    private Endpoints endpoints = new Endpoints();

    /**
     * Wrapper de configuration JWT
     */
    @Data
    public static class Jwt {

        /**
         * Constructeur par défaut
         */
        public Jwt() {
            super();
        }

        /**
         * Cle secrete JWT encodee en base64
         */
        private String secret;

        /**
         * Duree de validite du token d'acces en heures (defaut: 24h)
         */
        private int accessTokenExpirationHours = 24;

        /**
         * Duree de validite du token remember-me en jours (defaut: 365 jours)
         */
        private int rememberMeTokenExpirationDays = 365;

        /**
         * Duree de validite du token stocke en heures (defaut: 2h)
         */
        private int storedTokenExpirationHours = 2;
    }

    /**
     * Wrapper de configuration Cors
     */
    @Data
    public static class Cors {

        /**
         * Constructeur par défaut
         */
        public Cors() {
            super();
        }

        /**
         * Origines autorisees pour CORS (separees par des virgules)
         */
        private String allowedOrigins = "http://localhost,http://localhost:4200";

        /**
         * Methodes HTTP autorisees
         */
        private String allowedMethods = "GET,POST,PUT,PATCH,DELETE,OPTIONS";

        /**
         * Headers autorises
         */
        private String allowedHeaders = "*";

        /**
         * Activer les credentials
         */
        private boolean allowCredentials = true;

        /**
         * Duree du cache preflight en secondes
         */
        private long maxAge = 3600L;
    }

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

    /**
     * Wrapper de configuration Endpoints
     */
    @Data
    public static class Endpoints {

        /**
         * Constructeur par défaut
         */
        public Endpoints() {
            super();
        }

        /**
         * Chemin de base pour les endpoints d'authentification
         */
        private String authBasePath = "/authorize";

        /**
         * Chemin de base pour les endpoints d'enregistrement
         */
        private String registerBasePath = "/register";

        /**
         * Activer le logging des requetes
         */
        private boolean enableRequestLogging = false;
    }
}
