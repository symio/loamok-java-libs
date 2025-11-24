# Librairies Java Loamok

Bibliotheque de librairies Java / Spring Boot

## Fonctionnalites

### o2springsecurity : 

Version Stable active : 1.0.6

Version Développement active : 1.0.7-SNAPSHOT
 
- Authentification OAuth2 Client Credentials avec JWT
- Gestion des utilisateurs (création, activation, désactivation)
- Challenge de sécurité pour reinitialisation de mot de passe
- Tokens Remember Me
- Systeme d'e-mail avec templates
- Protection CORS configurable
- Signature client pour sécurité renforcée

### emailtemplates : 

Version Stable active : 1.0.1

Version Développement active : 1.0.1-SNAPSHOT
 
- Systeme d'e-mail avec templates

## Installation

Lire la note d'information Github au sujet du registre maven/gradle : 

1. gradle : [https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package](https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)
2. maven : [https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry](https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

Prétez particulièrement attention à : [Authentification auprès de GitHub Packages](https://docs.github.com/fr/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authentification-avec-un-personal-access-token)

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
    // Loamok
    implementation 'org.loamok.libs:<nom_de_la_lib_ici>:<version>'
    ....
}
```

### Maven

Comme pour Gradle, le registre de packages GitHub (maven.pkg.github.com) exige 
une authentification pour télécharger les artefacts, même s'ils sont publics.

Le processus se fait en deux étapes :

1. pom.xml : Déclarer la dépendance et le dépôt (le "repository").
2. settings.xml : Configurer les credentials (identifiants) pour ce dépôt.

#### 1. Configuration du pom.xml

Vous devez ajouter le dépôt (```<repository>```) en plus de la dépendance (```<dependency>```).

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
        <artifactId>[nom_de_la_lib_ici]</artifactId>
        <version>[version]</version>
    </dependency>
</dependencies>
```

#### 2. Configuration de l'authentification (settings.xml)

Vous devez ajouter un bloc ```<server>``` dont l'```<id>``` correspond exactement 
à l'<id> que vous avez défini dans le pom.xml (GitHubPackages-Loamok).

```xml
<settings>
  <servers>
    <server>
      <id>GitHubPackages-Loamok</id>
      <username>VOTRE_NOM_UTILISATEUR_GITHUB</username>
      <password>VOTRE_TOKEN_GITHUB</password>
    </server>
  </servers>
</settings>
```

## License

MIT

## Auteur

Huby Franck
