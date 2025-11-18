package org.loamok.libs.o2springsecurity.config;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loamok.libs.o2springsecurity.entity.Role;
import org.loamok.libs.o2springsecurity.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Configuration pour Spring Data Rest
 *
 * @author Huby Franck
 */
@Configuration
public class SpringDataRestConfig implements RepositoryRestConfigurer {

    /**
     * Journalisation
     */
    protected final Log logger = LogFactory.getLog(getClass());
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Setter pour configuration CORS
     * 
     * @param corsConfigurationSource CorsConfigurationSource
     */
    @Autowired
    public SpringDataRestConfig(@Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * Configuration des repositories
     * 
     * @param config Configuration
     * @param cors Registre Cors
     */
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        // Exposer les IDs dans les réponses JSON
        config.exposeIdsFor(User.class, Role.class);

        CorsConfiguration corsConfig = null;

        if (corsConfigurationSource instanceof UrlBasedCorsConfigurationSource uSource) {
            corsConfig = uSource.getCorsConfigurations().get("/**");
        }

        if (corsConfig == null) {
            logger.warn("Aucun CorsConfiguration trouvé pour '/**'. Abandon de la configuration CORS Spring Data REST.");
            return;
        }

        String[] originsArray = safeArray(corsConfig.getAllowedOrigins());
        String[] methodsArray = safeArray(corsConfig.getAllowedMethods());
        String[] headersArray = safeArray(corsConfig.getAllowedHeaders());

        // Activer CORS pour le frontend
        cors.addMapping("/**")
                .allowedOrigins(originsArray)
                .allowedMethods(methodsArray)
                .allowedHeaders(headersArray)
                .allowCredentials(corsConfig.getAllowCredentials())
                .maxAge(corsConfig.getMaxAge());
    }

    private static String[] safeArray(List<String> list) {
        return (list == null || list.isEmpty()) ? new String[0] : list.toArray(String[]::new);
    }
}
