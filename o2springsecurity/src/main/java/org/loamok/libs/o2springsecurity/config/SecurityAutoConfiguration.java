package org.loamok.libs.o2springsecurity.config;

import org.loamok.libs.o2springsecurity.manager.EmailManager;
import org.loamok.libs.o2springsecurity.manager.EmailService;
import org.loamok.libs.o2springsecurity.manager.UserManager;
import org.loamok.libs.o2springsecurity.manager.UserService;
import org.loamok.libs.o2springsecurity.repository.RoleRepository;
import org.loamok.libs.o2springsecurity.repository.UserRepository;
import org.loamok.libs.o2springsecurity.LoggingFilter;
import org.loamok.libs.o2springsecurity.jwt.JwtService;
import org.loamok.libs.o2springsecurity.jwt.JwtServiceImpl;
import org.loamok.libs.o2springsecurity.oauth2.OAuth2Service;
import org.loamok.libs.o2springsecurity.oauth2.OAuth2ServiceImpl;
import org.loamok.libs.o2springsecurity.util.ClientSignatureBuilder;
import org.loamok.libs.o2springsecurity.util.ClientSignatureUtil;
import org.loamok.libs.o2springsecurity.util.SpringContextUtil;
import org.loamok.libs.o2springsecurity.web.AuthenticationController;
import org.loamok.libs.o2springsecurity.web.UserProfileController;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration automatique pour le module de securite Loamok.
 * Active automatiquement tous les composants necessaires pour l'authentification JWT/OAuth2.
 * Wrapper fournissant des beans
 * 
 * @author Huby Franck
 */
@AutoConfiguration
@ConditionalOnClass({EnableWebSecurity.class, JwtServiceImpl.class})
@EnableConfigurationProperties(LoamokSecurityProperties.class)
@ComponentScan(basePackages = {
    "org.loamok.libs.o2.spring.security",
    "org.loamok.libs.o2springsecurity.dto.email",
    "org.loamok.libs.o2springsecurity.dto.request",
    "org.loamok.libs.o2springsecurity.dto.response",
    "org.loamok.libs.o2springsecurity.manager",
    "org.loamok.libs.o2springsecurity.util",
    "org.loamok.libs.o2springsecurity.web",
})
@EnableJpaRepositories(basePackages = "org.loamok.libs.o2springsecurity.repository")
@EntityScan(basePackages = "org.loamok.libs.o2springsecurity.entity")
@Import({
    JwtAppConfigImpl.class,
    SecurityConfig.class,
    CorsAutoConfiguration.class,
    JacksonConfig.class
})
public class SecurityAutoConfiguration {

    /**
     * Utilitaire de génération de signature client
     * 
     * @return ClientSignatureUtil
     */
    @Bean
    @ConditionalOnMissingBean
    public ClientSignatureUtil clientSignatureUtil() {
        return new ClientSignatureBuilder();
    }

    /**
     * Utilitaire pour contexte Spring
     * 
     * @return SpringContextUtil
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    /**
     * Service JWT
     * 
     * @return JwtService
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService() {
        return new JwtServiceImpl();
    }

    /**
     * Service Oauth2
     * 
     * @param userRepository UserRepository
     * @param passwordEncoder org.springframework.security.crypto.password.PasswordEncoder
     * @param jwtService JwtService
     * @return Oauth2Service
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2Service oauth2Service(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        return new OAuth2ServiceImpl(userRepository, passwordEncoder, jwtService);
    }

    /**
     * Email manager
     * 
     * @return EmailService
     */
    @Bean
    @ConditionalOnMissingBean
    public EmailService emailService() {
        return new EmailManager();
    }

    /**
     * User service
     * 
     * @param userRepository UserRepository
     * @param roleRepository RoleRepository
     * @param clientSignatureUtil ClientSignatureUtil
     * @param emailService EmailService
     * @return UserService
     */
    @Bean
    @ConditionalOnMissingBean
    public UserService userService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ClientSignatureUtil clientSignatureUtil,
            EmailService emailService) {
        return new UserManager();
    }

    /**
     * Filtre de journalisation
     * 
     * @return LoggingFilter
     */
    @Bean
    @ConditionalOnMissingBean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    /**
     * Contrôleur d'identification
     * 
     * @param oauth2Service Oauth2Service
     * @param clientSignatureUtil ClientSignatureUtil
     * @param jwtService JwtService
     * @return AutheticationController
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationController authenticationController(
            OAuth2Service oauth2Service,
            ClientSignatureUtil clientSignatureUtil,
            JwtService jwtService) {
        return new AuthenticationController(oauth2Service, clientSignatureUtil, jwtService);
    }

    /**
     * Controleur de gestion du profil utilisateur
     * 
     * @param userService UserService
     * @return UserProfileController
     */
    @Bean
    @ConditionalOnMissingBean
    public UserProfileController userProfileController(UserService userService) {
        return new UserProfileController(userService);
    }
}
