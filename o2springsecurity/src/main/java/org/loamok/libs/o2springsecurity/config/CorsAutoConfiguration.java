package org.loamok.libs.o2springsecurity.config;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Classe d'auto configuration des CORS de l'application
 *
 * @author Huby Franck
 */
@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsAutoConfiguration {
    private String allowedOrigins;
    /**
     * Journalisation
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Stocke les origines autorisées (setter)
     * 
     * @param allowedOrigins Liste des origines
     */
    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
    
    /**
     * Défini la configuration source cors 
     * 
     * @return la configuration Cors
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        
        logger.info("==================== CORS CONFIGURATION ====================");
        logger.info("corsConfigurationSource");
        logger.info("Variable CORS_ALLOWED_ORIGINS chargée depuis .env : '" + allowedOrigins + "'");
        
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins = Arrays.stream(allowedOrigins.split(","))
            .map(String::trim)
            .toList();
        
        logger.info("origins: " + origins.toString());
        logger.info("==========================================================");
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
