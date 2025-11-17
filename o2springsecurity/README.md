# Loamok Oauth2 Spring Security

Bibliotheque Spring Boot pour l'authentification OAuth2 avec JWT, 
incluant la gestion des utilisateurs et les workflows de securite.

## Fonctionnalites

- Authentification OAuth2 Client Credentials avec JWT
- Gestion des utilisateurs (creation, activation, desactivation)
- Challenge de securite pour reinitialisation de mot de passe
- Tokens Remember Me
- Systeme d'email avec templates
- Protection CORS configurable
- Signature client pour securite renforcee

## Installation

Lire la note d'information Github au sujet du registre maven/gradle : 

1. gradle : [https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package](https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)
2. maven : [https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry](https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

Pretez particulierement attention à : [Authentification auprès de GitHub Packages](https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authentification-avec-un-personal-access-token)

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
    implementation 'org.loamok.libs:o2springsecurity:1.0.2-SNAPSHOT'
    
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
        <version>1.0.2-SNAPSHOT</version>
    </dependency>
    
    <!-- Dependencies requises -->
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

La bibliotheque necessite la configuration de plusieurs proprietes Spring Boot standard en plus de ses propres proprietes.

#### Proprietes obligatoires

```yaml
# ============================================================
# CONFIGURATION LOAMOK SECURITY (proprietes de la bibliotheque)
# ============================================================
loamok:
  security:
    jwt:
      # Cle secrete JWT encodee en base64 (OBLIGATOIRE)
      # Generation : echo -n "VotreSecretTresLong" | base64
      secret: ${JWT_SECRET}
      
      # Duree de validite des tokens (optionnel, valeurs par defaut indiquees)
      access-token-expiration-hours: 24        # Defaut: 24h
      remember-me-token-expiration-days: 365   # Defaut: 365 jours
      stored-token-expiration-hours: 2         # Defaut: 2h
      
    cors:
      # Origines autorisees pour CORS (OBLIGATOIRE)
      allowed-origins: http://localhost:4200,http://localhost:8080
      
      # Configuration CORS avancee (optionnel)
      allowed-methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600
      
    email:
      # URL du frontend (pas du backend). Utilisée pour générer les liens 
      # cliquables dans les emails d'activation et de reset de mot de passe.
      # Exemple : `http://localhost:4200` (OBLIGATOIRE)
      base-url: http://localhost:4200
      
      # Email de l'administrateur pour les notifications (OBLIGATOIRE)
      admin-email: admin@example.com
      
      # Duree de validite des cles d'activation par email (optionnel)
      key-validity-hours: 1  # Defaut: 1h
      
    endpoints:
      # Chemins de base des endpoints (optionnel)
      auth-base-path: /authorize      # Defaut: /authorize
      register-base-path: /register   # Defaut: /register
      enable-request-logging: false   # Defaut: false

# ============================================================
# CONFIGURATION SPRING BOOT (dependances externes requises)
# ============================================================

# Configuration base de donnees (OBLIGATOIRE)
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/myapp
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    
  # Configuration JPA/Hibernate (OBLIGATOIRE)
  jpa:
    hibernate:
      ddl-auto: update  # ou validate en production
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MariaDBDialect
        
  # Configuration serveur mail (OBLIGATOIRE pour les emails)
  mail:
    host: smtp.example.com
    port: 587
    username: ${MAIL_USERNAME}  # Utilise comme adresse expediteur
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls.enable: true
```

#### Variables d'environnement recommandees

Creez un fichier `.env` ou definissez ces variables :

```bash
# JWT
JWT_SECRET=VGhpc0lzQVNlY3JldEtleUVuY29kZWRJbkJhc2U2NA==

# CORS
CORS_ORIGINS=http://localhost:4200,http://localhost:8080

# Application
BASE_URL=http://localhost:8080

# Base de donnees
DB_USER=myuser
DB_PASSWORD=mypassword

# Mail (OBLIGATOIRE - utilise par la lib pour envoyer des emails)
MAIL_USERNAME=noreply@example.com
MAIL_PASSWORD=mailpassword
```

### Configuration complete avec toutes les options

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

## Proprietes de configuration

### Proprietes de la bibliotheque (`loamok.security.*`)

| Propriete | Type | Defaut | Description |
|-----------|------|--------|-------------|
| `loamok.security.jwt.secret` | String | **OBLIGATOIRE** | Cle secrete JWT encodee en base64 |
| `loamok.security.jwt.access-token-expiration-hours` | int | 24 | Duree de validite du token d'acces (heures) |
| `loamok.security.jwt.remember-me-token-expiration-days` | int | 365 | Duree de validite du token remember-me (jours) |
| `loamok.security.jwt.stored-token-expiration-hours` | int | 2 | Duree de validite du token stocke serveur (heures) |
| `loamok.security.cors.allowed-origins` | String | **OBLIGATOIRE** | Origines autorisees CORS (separees par virgules) |
| `loamok.security.email.base-url` | String | **OBLIGATOIRE** | URL de base pour les liens dans les emails |
| `loamok.security.email.admin-email` | String | **OBLIGATOIRE** | Email de l'administrateur pour notifications |
| `loamok.security.email.key-validity-hours` | int | 1 | Duree de validite des cles d'activation email (heures) |

### Dependances Spring Boot requises

La bibliotheque depend de ces proprietes Spring Boot standard :

| Propriete | Utilisation | Obligatoire |
|-----------|-------------|-------------|
| `spring.datasource.url` | Connexion base de donnees pour stocker users/roles/tokens | Oui |
| `spring.datasource.username` | Identifiant base de donnees | Oui |
| `spring.datasource.password` | Mot de passe base de donnees | Oui |
| `spring.mail.host` | Serveur SMTP pour envoi d'emails | Oui |
| `spring.mail.port` | Port SMTP | Oui |
| `spring.mail.username` | Compte SMTP (utilise comme adresse expediteur) | Oui |
| `spring.mail.password` | Mot de passe compte SMTP | Oui |

## Utilisation

### 1. Creation des tables

La bibliotheque utilise Hibernate pour creer automatiquement les tables necessaires:
- `users`
- `roles`

### 2. Initialisation des roles

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

### 3. Enregistrement d'un utilisateur

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

### 4. Authentification

```bash
POST /authorize/token
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&client_id=user@example.com&client_secret=SecureP@ss123&scope=access rememberme
```

Reponse:
```json
{
  "access_token": "eyJhbGc...",
  "remember_me_token": "eyJhbGc...",
  "token_type": "Bearer",
  "expires_in": 86400,
  "scope": "access"
}
```

### 5. Utilisation du token

```bash
GET /api/resource
Authorization: Bearer eyJhbGc...
```

## Endpoints disponibles

### Authentification (`/authorize`)

- `POST /authorize/token` - Obtenir un token d'acces
- `POST /authorize/refresh` - Rafraichir avec access_token
- `POST /authorize/remembered` - Rafraichir avec remember_me_token
- `POST /authorize/cleanup` - Deconnexion

### Enregistrement (`/register`)

- `POST /register/activate` - Activer un compte
- `POST /register/deactivate` - Desactiver un compte
- `POST /register/password-lost/step1` - Demande de reset
- `POST /register/password-lost/step2` - Confirmation du reset
- `POST /register/password-lost/step1/deactivate` - Annuler step1
- `POST /register/password-lost/step2/deactivate` - Annuler step2

## Personnalisation

### Surcharge des services

Vous pouvez surcharger n'importe quel bean de la bibliotheque:

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

### Messages email personnalises

Implementez l'interface `EmailMessage` et declarez votre bean:

```java
@Service
@Primary
public class CustomEmailMessages implements EmailMessage {
    @Override
    public String getEmailMessage(String messageKey, Map<String, String> substitutions) {
        // Votre implementation
    }
    
    @Override
    public String getEmailTitle(String titleKey, Map<String, String> substitutions) {
        // Votre implementation
    }
}
```

## Exigences

- Java 21+
- Spring Boot 3.5+
- Base de donnees compatible JPA (MariaDB, MySQL, PostgreSQL, H2...)
- Serveur SMTP configure pour l'envoi d'emails

## Securite

- Les mots de passe sont hashes avec BCrypt
- Les tokens JWT sont signes avec HMAC-SHA256
- Support de la signature client pour detecter les usurpations
- Validation stricte des tokens
- Protection CORS configurable

## Troubleshooting

### Erreur: "Could not autowire. No beans of type 'JavaMailSender' found"

Verifiez que vous avez bien configure `spring.mail.*` dans votre `application.yml` et que la dependance `spring-boot-starter-mail` est presente.

### Erreur: "Invalid JWT secret"

La cle JWT doit etre encodee en base64. Generez-la avec :
```bash
echo -n "VotreSecretTresLongEtAleatoire" | base64
```

### Les emails ne sont pas envoyes

Verifiez :
1. Configuration SMTP correcte (`spring.mail.host`, `spring.mail.port`)
2. Credentials valides (`spring.mail.username`, `spring.mail.password`)
3. Configuration `loamok.security.email.base-url` et `loamok.security.email.admin-email`

## License

MIT

## Auteur

Huby Franck