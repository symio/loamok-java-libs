# Loamok E-mails templates

Librairie de gestion des templates E-mails

Version Stable active : 1.0.1

Version Développement active : 1.0.1-SNAPSHOT

## Fonctionnalités
 
- Systeme d'e-mail avec templates

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
    implementation 'org.loamok.libs:emailtemplates:1.0.1'
    
    // Dépendances Spring Boot requises
    implementation 'org.springframework.boot:spring-boot-starter-mail'
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
        <artifactId>emailtemplates</artifactId>
        <version>1.0.1</version>
    </dependency>
    
    <!-- Dépendances requises -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
</dependencies>
```

## Configuration

### Configuration minimale requise

La bibliothèque nécessite la configuration de plusieurs propriétés Spring Boot 
standard en plus de ses propres propriétés.

#### Propriétés obligatoires

```yaml
# Fichier de configuration exemple pour l'utilisation de o2springsecurity
# Copiez ce fichier vers application.yml, application-secrets.yml et adaptez les valeurs

# ============================================================
# CONFIGURATION LOAMOK SECURITY (Propriétés de la bibliothèque)
# ============================================================
loamok:
  emails:
    # Configuration E-mail
    email:
      # OBLIGATOIRE : URL de base du FRONTEND pour les liens dans les e-mails
      # Exemple: Si base-url=http://localhost:4200, les liens seront:
      # - http://localhost:4200/register/activate?key=xxx
      # - http://localhost:4200/register/password-lost2?key=xxx
      base-url: ${BASE_URL:http://localhost:4200}
      
      # OBLIGATOIRE : E-mail administrateur pour recevoir les notifications
      admin-email: ${ADMIN_EMAIL:admin@example.com}

      # Nom de votre application à afficher dans les e-mails
      application-name: Todo
    
# ============================================================
# CONFIGURATION SPRING BOOT (Dépendances externes OBLIGATOIRES)
# ============================================================
spring:
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
# Mail (OBLIGATOIRE - utilisé par la lib pour envoyer des emails)
MAIL_USERNAME=noreply@example.com
MAIL_PASSWORD=mailpassword
```

### Configuration complète avec toutes les options

```yaml
loamok:
  emails:
    email:
      base-url: ${BASE_URL}
      admin-email: admin@example.com
      application-name: Todo

spring:
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
| `loamok.emails.email.base-url` | String | **OBLIGATOIRE** | URL de base pour les liens dans les emails |
| `loamok.emails.email.admin-email` | String | **OBLIGATOIRE** | Email de l'administrateur pour notifications |
| `loamok.emails.email.key-validity-hours` | int | 1 | Durée de validité des clés d'activation email (heures) |

### Dépendances Spring Boot requises

La bibliothèque dépend de ces propriétés Spring Boot standard :

| Propriété | Utilisation | Obligatoire |
|-----------|-------------|-------------|
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
    "org.loamok.libs.emailtemplates"
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
- Serveur SMTP configuré pour l'envoi d'emails

## Troubleshooting

### Erreur: "Could not autowire. No beans of type 'JavaMailSender' found"

Vérifiez que vous avez bien configuré `spring.mail.*` dans votre `application.yml` 
et que la dépendance `spring-boot-starter-mail` est présente.

### Les emails ne sont pas envoyés

Vérifiez :
1. Configuration SMTP correcte (`spring.mail.host`, `spring.mail.port`)
2. Credentials valides (`spring.mail.username`, `spring.mail.password`)
3. Configuration `loamok.emails.email.base-url` et `loamok.emails.email.admin-email`

## License

MIT

## Auteur

Huby Franck