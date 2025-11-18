# Loamok Oauth2 Spring Security

Bibliothèque Spring Boot pour l'authentification OAuth2 avec JWT, 
incluant la gestion des utilisateurs et les workflows de sécurité.

## Fonctionnalités

- Authentification OAuth2 Client Credentials avec JWT
- Gestion des utilisateurs (création, activation, désactivation)
- Challenge de sécurité pour réinitialisation de mot de passe
- Tokens Remember Me
- Système d'email avec templates
- Protection CORS configurable
- Signature client pour sécurité renforcée

## Installation

Lire la note d'information Github au sujet du registre maven/gradle : 

1. gradle : [https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package](https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)
2. maven : [https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry](https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

Prêtez particulièrement attention à : [Authentification auprès de GitHub Packages](https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authentification-avec-un-personal-access-token)

Si vous utilisez un builder docker depuis un de nos projets le script "setup-environment.sh" vous demandera
votre nom d'utilisateur github et votre token.

### Gradle

```gradle
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages-Loamok"
        url = uri("https://maven.pkg.github.com/symio/loamok-java-libs") 
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'org.loamok.libs:o2springsecurity:1.0.3'
    
    // Dépendances Spring Boot requises
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-mail'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>central</id>
        <name>Maven Central</name>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
    <repository>
        <id>GitHubPackages-Loamok</id>
        <name>GitHubPackages-Loamok</name>
        <url>https://maven.pkg.github.com/symio/loamok-java-libs</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.loamok.libs</groupId>
        <artifactId>o2springsecurity</artifactId>
        <version>1.0.3</version>
    </dependency>
    
    <!-- Dépendances requises -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
</dependencies>
```

## Configuration

### Configuration minimale requise

La bibliothèque nécessite la configuration de plusieurs propriétés Spring Boot standard en plus de ses propres propriétés.

#### Propriétés obligatoires

```yaml
# Fichier de configuration exemple pour l'utilisation de o2springsecurity
# Copiez ce fichier vers application.yml, application-secrets.yml et adaptez les valeurs

# ============================================================
# CONFIGURATION LOAMOK SECURITY (Propriétés de la bibliothèque)
# ============================================================
loamok:
  security:
    # Configuration JWT
    jwt:
      # OBLIGATOIRE : Clé secrète encodée en base64
      # Génération : echo -n "VotreSecretTresLongEtAleatoire" | base64
      # à mettre dans application-secrets.yml
      secret: ${JWT_SECRET:VGhpc0lzQURldkp3dFNlY3JldEZvckxvY2FsVGVzdGluZ09ubHkxMjM0NQ==}
      
      # Durée de validité du token d'accès en heures (défaut: 24)
      access-token-expiration-hours: 24
      
      # Durée de validité du token remember-me en jours (défaut: 365)
      remember-me-token-expiration-days: 365
      
      # Durée de validité du token stocké côté serveur en heures (défaut: 2)
      stored-token-expiration-hours: 2
    
    # Configuration CORS
    cors:
      # OBLIGATOIRE : Origines autorisées (séparées par virgules)
      allowed-origins: ${CORS_ORIGINS:http://localhost:4200,http://localhost:8080}
      
      # Méthodes HTTP autorisées
      allowed-methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
      
      # Headers autorisés (* pour tous)
      allowed-headers: "*"
      
      # Autoriser les crédentials (headers auth)
      allow-credentials: true
      
      # Durée du cache preflight en secondes
      max-age: 3600
    
    # Configuration E-mail
    email:
      # OBLIGATOIRE : URL de base du FRONTEND pour les liens dans les e-mails
      # Exemple: Si base-url=http://localhost:4200, les liens seront:
      # - http://localhost:4200/register/activate?key=xxx
      # - http://localhost:4200/register/password-lost2?key=xxx
      base-url: ${BASE_URL:http://localhost:4200}
      
      # OBLIGATOIRE : E-mail administrateur pour recevoir les notifications
      admin-email: ${ADMIN_EMAIL:admin@example.com}
      
      # Durée de validité des clés d'activation e-mail en heures (défaut: 1)
      key-validity-hours: 1
    
    # Configuration des endpoints
    endpoints:
      # Chemin de base pour les endpoints d'authentification
      auth-base-path: /authorize
      
      # Chemin de base pour les endpoints d'enregistrement
      register-base-path: /register
      
      # Activer le logging détaillé des requêtes
      enable-request-logging: false

# ============================================================
# CONFIGURATION SPRING BOOT (Dépendances externes OBLIGATOIRES)
# ============================================================
spring:
  # Configuration base de données (OBLIGATOIRE)
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mariadb://localhost:3306/myapp}
    # à mettre dans application-secrets.yml
    username: ${SPRING_DATASOURCE_USERNAME:dbuser}
    password: ${SPRING_DATASOURCE_PASSWORD:dbpassword}
  
  # Configuration JPA/Hibernate (OBLIGATOIRE)
  jpa:
    # Mode de création/mise à jour du schéma
    # - create-drop : Recrée le schéma à chaque démarrage (dev uniquement)
    # - update : Met à jour le schéma automatiquement (dev/test)
    # - validate : Vérifie le schéma sans le modifier (production)
    # - none : Pas de gestion automatique (production avec migrations)
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:validate}
    
    # Afficher les requêtes SQL dans les logs (dev uniquement)
    show-sql: false
    
    # Propriétés Hibernate
    properties:
      hibernate:
        # Dialecte base de données 
        # hibernate l'auto-détecte généralement
        # dialect: org.hibernate.dialect.MariaDBDialect
        
        # Formater les requêtes SQL dans les logs
        format-sql: true
        
        # Désactiver l'Open Session In View (recommandé)
        open-in-view: false
  
  # Configuration serveur mail (OBLIGATOIRE pour envoyer des e-mails)
  mail:
    # Serveur SMTP
    host: ${SPRING_MAIL_HOST:smtp.example.com}
    
    # Port SMTP (587 pour STARTTLS, 465 pour SSL, 25 pour non-sécurisé)
    port: ${SPRING_MAIL_PORT:587}
    
    # Identifiant compte SMTP (utilisé aussi comme adresse d'expéditeur par défaut)
    username: ${SPRING_MAIL_USERNAME:noreply@example.com}
    
    # Mot de passe compte SMTP
    password: ${SPRING_MAIL_PASSWORD:mailpassword}
    
    # Propriétés SMTP avancées
    properties:
      mail:
        smtp:
          # Activer l'authentification SMTP
          auth: true
          
          # Activer STARTTLS pour sécuriser la connexion
          starttls.enable: true
```

#### Variables d'environnement recommandées

Créez un fichier `.env` ou définissez ces variables :

```bash
# JWT
JWT_SECRET=VGhpc0lzQVNlY3JldEtleUVuY29kZWRJbkJhc2U2NA==

# CORS
CORS_ORIGINS=http://localhost:4200,http://localhost:8080

# Application
BASE_URL=http://localhost:8080

# Base de données
DB_USER=myuser
DB_PASSWORD=mypassword

# Mail (OBLIGATOIRE - utilisé par la lib pour envoyer des emails)
MAIL_USERNAME=noreply@example.com
MAIL_PASSWORD=mailpassword
```

### Configuration complète avec toutes les options

```yaml
loamok:
  security:
    jwt:
      secret: ${JWT_SECRET}
      access-token-expiration-hours: 24
      remember-me-token-expiration-days: 365
      stored-token-expiration-hours: 2
    cors:
      allowed-origins: ${CORS_ORIGINS}
      allowed-methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600
    email:
      base-url: ${BASE_URL}
      key-validity-hours: 1
      admin-email: admin@example.com
    endpoints:
      auth-base-path: /authorize
      register-base-path: /register
      enable-request-logging: false

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    hikari:
      minimum-idle: 1
      maximum-pool-size: 5
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format-sql: true
  mail:
    host: ${SPRING_MAIL_HOST}
    port: ${SPRING_MAIL_PORT:587}
    username: ${SPRING_MAIL_USERNAME}
    password: ${SPRING_MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls.enable: true
```

## Propriétés de configuration

### Propriétés de la bibliothèque (`loamok.security.*`)

| Propriété | Type | Défaut | Description |
|-----------|------|--------|-------------|
| `loamok.security.jwt.secret` | String | **OBLIGATOIRE** | Clé secrète JWT encodée en base64 |
| `loamok.security.jwt.access-token-expiration-hours` | int | 24 | Durée de validité du token d'accès (heures) |
| `loamok.security.jwt.remember-me-token-expiration-days` | int | 365 | Durée de validité du token remember-me (jours) |
| `loamok.security.jwt.stored-token-expiration-hours` | int | 2 | Durée de validité du token stocké serveur (heures) |
| `loamok.security.cors.allowed-origins` | String | **OBLIGATOIRE** | Origines autorisées CORS (séparées par virgules) |
| `loamok.security.email.base-url` | String | **OBLIGATOIRE** | URL de base pour les liens dans les emails |
| `loamok.security.email.admin-email` | String | **OBLIGATOIRE** | Email de l'administrateur pour notifications |
| `loamok.security.email.key-validity-hours` | int | 1 | Durée de validité des clés d'activation email (heures) |

### Dépendances Spring Boot requises

La bibliothèque dépend de ces propriétés Spring Boot standard :

| Propriété | Utilisation | Obligatoire |
|-----------|-------------|-------------|
| `spring.datasource.url` | Connexion base de données pour stocker users/roles/tokens | Oui |
| `spring.datasource.username` | Identifiant base de données | Oui |
| `spring.datasource.password` | Mot de passe base de données | Oui |
| `spring.mail.host` | Serveur SMTP pour envoi d'emails | Oui |
| `spring.mail.port` | Port SMTP | Oui |
| `spring.mail.username` | Compte SMTP (utilisé comme adresse expéditeur) | Oui |
| `spring.mail.password` | Mot de passe compte SMTP | Oui |

## Utilisation

### 1. Adaptez votre application

Exemple avec une application SpringBoot "Todo" : 

```java
package org.loamok.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {
    "org.loamok.todo",
    "org.loamok.libs.o2springsecurity"
})
public class TodoApplication  extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TodoApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

}
```

### 2. Création des tables

La bibliothèque utilise Hibernate pour créer automatiquement les tables nécessaires:
- `users`
- `roles`

### 3. Initialisation des rôles

```java
@Component
public class DataInitializer {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @PostConstruct
    public void init() {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder()
                .role("ROLE_USER")
                .isAdmin(false)
                .build());
                
            roleRepository.save(Role.builder()
                .role("ROLE_ADMIN")
                .isAdmin(true)
                .build());
        }
    }
}
```

### 4. Enregistrement d'un utilisateur

```bash
POST /users
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecureP@ss123",
  "name": "Doe",
  "firstname": "John",
  "gdproptin": true
}
```

### 5. Authentification

```bash
POST /authorize/token
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&client_id=user@example.com&client_secret=SecureP@ss123&scope=access rememberme
```

Réponse:
```json
{
  "access_token": "eyJhbGc...",
  "remember_me_token": "eyJhbGc...",
  "token_type": "Bearer",
  "expires_in": 86400,
  "scope": "access"
}
```

### 6. Utilisation du token

```bash
GET /api/resource
Authorization: Bearer eyJhbGc...
```

## Endpoints disponibles

### Authentification (`/authorize`)

- `POST /authorize/token` - Obtenir un token d'accès
- `POST /authorize/refresh` - Rafraîchir avec access_token
- `POST /authorize/remembered` - Rafraîchir avec remember_me_token
- `POST /authorize/cleanup` - Déconnexion

### Enregistrement (`/register`)

- `POST /register/activate` - Activer un compte
- `POST /register/deactivate` - Désactiver un compte
- `POST /register/password-lost/step1` - Demande de reset
- `POST /register/password-lost/step2` - Confirmation du reset
- `POST /register/password-lost/step1/deactivate` - Annuler step1
- `POST /register/password-lost/step2/deactivate` - Annuler step2

## Personnalisation

### Surcharge des services

Vous pouvez surcharger n'importe quel bean de la bibliothèque:

```java
@Configuration
public class CustomSecurityConfig {
    
    @Bean
    @Primary
    public UserService customUserService() {
        return new MyCustomUserService();
    }
}
```

### Messages email personnalisés

Implémentez l'interface `EmailMessage` et déclarez votre bean:

```java
@Service
@Primary
public class CustomEmailMessages implements EmailMessage {
    @Override
    public String getEmailMessage(String messageKey, Map<String, String> substitutions) {
        // Votre implémentation
    }
    
    @Override
    public String getEmailTitle(String titleKey, Map<String, String> substitutions) {
        // Votre implémentation
    }
}
```

## Exigences

- Java 21+
- Spring Boot 3.5+
- Base de données compatible JPA (MariaDB, MySQL, PostgreSQL, H2...)
- Serveur SMTP configuré pour l'envoi d'emails

## Sécurité

- Les mots de passe sont hashés avec BCrypt
- Les tokens JWT sont signés avec HMAC-SHA256
- Support de la signature client pour détecter les usurpations
- Validation stricte des tokens
- Protection CORS configurable

## Troubleshooting

### Erreur: "Could not autowire. No beans of type 'JavaMailSender' found"

Vérifiez que vous avez bien configuré `spring.mail.*` dans votre `application.yml` et que la dépendance `spring-boot-starter-mail` est présente.

### Erreur: "Invalid JWT secret"

La clé JWT doit être encodée en base64. Générez-la avec :
```bash
echo -n "VotreSecretTresLongEtAleatoire" | base64
```

### Les emails ne sont pas envoyés

Vérifiez :
1. Configuration SMTP correcte (`spring.mail.host`, `spring.mail.port`)
2. Credentials valides (`spring.mail.username`, `spring.mail.password`)
3. Configuration `loamok.security.email.base-url` et `loamok.security.email.admin-email`

## License

MIT

## Auteur

Huby Franck