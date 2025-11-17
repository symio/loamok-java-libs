package org.loamok.libs.o2springsecurity.config;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Classe d'auto configuration des CORS pour la bibliotheque o2springsecurity
 * Configure les CORS depuis loamok.security.cors.*
 * 
 * IMPORTANT: Cette configuration est destinee aux endpoints de la lib (authorize, register).
 * L'application consommatrice peut avoir sa propre config CORS avec prefix "cors.*"
 * pour ses propres endpoints.
 *
 * @author Huby Franck
 */
@Configuration
public class CorsAutoConfiguration {
    
    private final LoamokSecurityProperties securityProperties;
    
    /**
     * Journalisation
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Constructeur avec injection de la configuration
     * 
     * @param securityProperties Configuration de la bibliotheque
     */
    public CorsAutoConfiguration(LoamokSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
    
    /**
     * Defini la configuration source cors depuis loamok.security.cors
     * 
     * @return la configuration Cors
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("==================== CORS CONFIGURATION ====================");
        logger.info("corsConfigurationSource");
        
        // Recupere la config CORS depuis LoamokSecurityProperties
        LoamokSecurityProperties.Cors corsConfig = securityProperties.getCors();
        String allowedOrigins = corsConfig.getAllowedOrigins();
        
        logger.info("Configuration CORS chargée depuis loamok.security.cors : '" + allowedOrigins + "'");
        
        CorsConfiguration config = new CorsConfiguration();

        // Parse les origines depuis la chaine CSV
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
            .map(String::trim)
            .toList();
        
        logger.info("origins: " + origins.toString());
        
        // Parse les methodes depuis la configuration
        List<String> methods = Arrays.stream(corsConfig.getAllowedMethods().split(","))
            .map(String::trim)
            .toList();
        logger.info("methods: " + methods.toString());
        
        // Parse les headers (ou utilise "*")
        List<String> headers = "*".equals(corsConfig.getAllowedHeaders()) 
            ? List.of("*")
            : Arrays.stream(corsConfig.getAllowedHeaders().split(","))
                .map(String::trim)
                .toList();
        logger.info("headers: " + headers.toString());
        logger.info("==========================================================");
        
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(methods);
        config.setAllowedHeaders(headers);
        config.setAllowCredentials(corsConfig.isAllowCredentials());
        config.setMaxAge(corsConfig.getMaxAge());
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}