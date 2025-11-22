package org.loamok.libs.o2springsecurity.config;

import java.util.function.Supplier;
import org.apache.commons.logging.*;
import org.loamok.libs.o2springsecurity.repository.UserRepository;
import org.loamok.libs.o2springsecurity.jwt.JwtAuthenticationFilter;
import org.loamok.libs.o2springsecurity.jwt.JwtService;
import org.loamok.libs.o2springsecurity.util.ClientSignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Configuration de sécurité
 * 
 * @author Huby Franck
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;
    
    @Autowired
    private LoamokSecurityProperties securityProperties;
    /**
     * Journalisation
     */
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Value("${springdoc.api-docs.enabled:false}")
    private boolean swaggerEnabled;

    /**
     * Chaîne du filtre de sécurité
     * 
     * @param http HttpSecurity
     * @param jwtService JwtService
     * @param userDetailsService UserDetailsService
     * @param userRepository UserRepository
     * @param csb ClientSignatureUtil
     * @return SecurityFilterChain
     * @throws Exception Relai des erreurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            UserRepository userRepository,
            ClientSignatureUtil csb
    ) throws Exception {
        logger.info("============================================================");
        logger.info("CHARGEMENT SECURITY FILTER CHAIN UNIQUE");
        logger.info("============================================================");

        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(
            jwtService,
            userDetailsService,
            userRepository,
            csb
        );
        
        // Recuperer les chemins configures
        String authPath = securityProperties.getEndpoints().getAuthBasePath();
        String registerPath = securityProperties.getEndpoints().getRegisterBasePath();

        http
            .authorizeHttpRequests(auth -> {
                logger.info("Configuration des regles d'autorisation...");
                logger.info("Auth path: "+ authPath);
                logger.info("Register path: "+ registerPath);
                
                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                
                auth.requestMatchers(HttpMethod.GET,  "/actuator/health").permitAll();
                
                // Routes publiques avec chemins dynamiques
                auth.requestMatchers(HttpMethod.POST, 
                    authPath + "/token",
                    authPath + "/refresh", 
                    authPath + "/cleanup",
                    authPath + "/remembered",
                    registerPath + "/activate",
                    registerPath + "/check-password",
                    registerPath + "/deactivate",
                    registerPath + "/password-lost/step1",
                    registerPath + "/password-lost/step1/deactivate",
                    registerPath + "/password-lost/step2",
                    registerPath + "/password-lost/step2/deactivate",
                    "/users"
                ).permitAll();
                
                if (swaggerEnabled) {
                    auth.requestMatchers(
                        "/v3/api-docs/**", 
                        "/swagger-ui/**", 
                        "/swagger-ui.html", 
                        "/swagger-resources/**", 
                        "/webjars/**"
                    ).permitAll();
                }
                
                // Routes ADMIN uniquement (nécessitent ROLE_ADMIN)
                auth.requestMatchers(HttpMethod.GET, "/roles/**").hasRole("ADMIN");
                auth.requestMatchers(HttpMethod.POST, "/roles/**").hasRole("ADMIN");
                auth.requestMatchers(HttpMethod.PUT, "/roles/**").hasRole("ADMIN");
                auth.requestMatchers(HttpMethod.PATCH, "/roles/**").hasRole("ADMIN");
                auth.requestMatchers(HttpMethod.DELETE, "/roles/**").hasRole("ADMIN");
                
                auth.requestMatchers(HttpMethod.GET, "/profile/**").hasRole("ADMIN");
                auth.requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN");
                auth.requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN");
                
                // Toutes les autres requêtes nécessitent le scope "access"
                auth.anyRequest().access(this::hasAccessScopeAndAuthenticated);
            })
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource));

        return http.build();
    }

    /**
     * Vérifie que l'utilisateur a le scope "access" ET est authentifié Cette
     * méthode ajoute une couche de sécurité supplémentaire
     * 
     * @param authentication Supplier<Authentication>
     * @param context RequestAuthorizationContext
     * @return AuthorizationDecision
     */
    private AuthorizationDecision hasAccessScopeAndAuthenticated(
            Supplier<Authentication> authentication, 
            RequestAuthorizationContext context
    ) {
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // Vérifier que l'utilisateur a le scope "access"
        boolean hasAccessScope = auth.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("SCOPE_access"));

        return new AuthorizationDecision(hasAccessScope);
    }

}
