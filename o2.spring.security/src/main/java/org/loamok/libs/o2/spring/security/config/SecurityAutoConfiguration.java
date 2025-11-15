package org.loamok.libs.o2.spring.security.config;

import org.loamok.libs.o2.spring.security.manager.EmailManager;
import org.loamok.libs.o2.spring.security.manager.EmailService;
import org.loamok.libs.o2.spring.security.manager.UserManager;
import org.loamok.libs.o2.spring.security.manager.UserService;
import org.loamok.libs.o2.spring.security.repository.RoleRepository;
import org.loamok.libs.o2.spring.security.repository.UserRepository;
import org.loamok.libs.o2.spring.security.LoggingFilter;
import org.loamok.libs.o2.spring.security.jwt.JwtService;
import org.loamok.libs.o2.spring.security.jwt.JwtServiceImpl;
import org.loamok.libs.o2.spring.security.oauth2.OAuth2Service;
import org.loamok.libs.o2.spring.security.oauth2.OAuth2ServiceImpl;
import org.loamok.libs.o2.spring.security.util.ClientSignatureBuilder;
import org.loamok.libs.o2.spring.security.util.ClientSignatureUtil;
import org.loamok.libs.o2.spring.security.util.SpringContextUtil;
import org.loamok.libs.o2.spring.security.web.AuthenticationController;
import org.loamok.libs.o2.spring.security.web.UserProfileController;
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

/**
 * Configuration automatique pour le module de securite Loamok.
 * Active automatiquement tous les composants necessaires pour l'authentification JWT/OAuth2.
 * 
 * @author Huby Franck
 */
@AutoConfiguration
@ConditionalOnClass({EnableWebSecurity.class, JwtServiceImpl.class})
@EnableConfigurationProperties(LoamokSecurityProperties.class)
@ComponentScan(basePackages = {
    "org.loamok.libs.o2.spring.security",
    "org.loamok.libs.o2.spring.security.dto.email",
    "org.loamok.libs.o2.spring.security.dto.request",
    "org.loamok.libs.o2.spring.security.dto.response",
    "org.loamok.libs.o2.spring.security.manager",
    "org.loamok.libs.o2.spring.security.util",
    "org.loamok.libs.o2.spring.security.web",
})
@EnableJpaRepositories(basePackages = "org.loamok.libs.o2.spring.security.repository")
@EntityScan(basePackages = "org.loamok.libs.o2.spring.security.entity")
@Import({
    JwtAppConfig.class,
    SecurityConfig.class,
    CorsAutoConfiguration.class,
    JacksonConfig.class
})
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ClientSignatureUtil clientSignatureUtil() {
        return new ClientSignatureBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService() {
        return new JwtServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2Service oauth2Service(
            UserRepository userRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        return new OAuth2ServiceImpl(userRepository, passwordEncoder, jwtService);
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailService emailService() {
        return new EmailManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserService userService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ClientSignatureUtil clientSignatureUtil,
            EmailService emailService) {
        return new UserManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationController authenticationController(
            OAuth2Service oauth2Service,
            ClientSignatureUtil clientSignatureUtil,
            JwtService jwtService) {
        return new AuthenticationController(oauth2Service, clientSignatureUtil, jwtService);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserProfileController userProfileController(UserService userService) {
        return new UserProfileController(userService);
    }
}
