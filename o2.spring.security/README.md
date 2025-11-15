# Loamok Spring Security

Bibliotheque Spring Boot pour l'authentification OAuth2 avec JWT, incluant la gestion des utilisateurs et les workflows de securite.

## Fonctionnalites

- Authentification OAuth2 Client Credentials avec JWT
- Gestion des utilisateurs (creation, activation, desactivation)
- Challenge de securite pour reinitialisation de mot de passe
- Tokens Remember Me
- Systeme d'email avec templates
- Protection CORS configurable
- Signature client pour securite renforcee

## Installation

### Gradle

```gradle
dependencies {
    implementation 'org.loamok:loamok-spring-security:1.0.0-SNAPSHOT'
}
```

### Maven

```xml
<dependency>
    <groupId>org.loamok</groupId>
    <artifactId>loamok-spring-security</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Configuration

### Configuration minimale

```yaml
loamok:
  security:
    jwt:
      secret: VGhpc0lzQVNlY3JldEtleQ== # base64 encoded
    cors:
      allowed-origins: http://localhost:4200,http://localhost:8080
    email:
      base-url: http://localhost:8080
      admin-email: admin@example.com

spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/myapp
    username: user
    password: pass
  mail:
    host: smtp.example.com
    port: 587
    username: noreply@example.com
    password: mailpass
```

### Configuration complete

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
```

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

## Securite

- Les mots de passe sont hashes avec BCrypt
- Les tokens JWT sont signes avec HMAC-SHA256
- Support de la signature client pour detecter les usurpations
- Validation stricte des tokens
- Protection CORS configurable

## License

MIT

## Auteur

Huby Franck
