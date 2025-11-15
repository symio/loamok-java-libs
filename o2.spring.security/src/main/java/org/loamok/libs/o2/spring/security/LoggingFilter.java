package org.loamok.libs.o2.spring.security; 

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collections;

/**
 * Filtre d'enregistrement des journaux
 * 
 * @author Huby Franck
 */
@Component
public class LoggingFilter implements Filter {
    /**
     * Constructeur par défaut
     */
    public LoggingFilter() {
        super();
    }

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // On log seulement au début d'une requête API pour ne pas surcharger
        if (httpRequest.getRequestURI().startsWith("/todo/")) {
            logger.info("================ INCOMING REQUEST ================");
            logger.info("Request URI: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
            Collections.list(httpRequest.getHeaderNames()).forEach(headerName ->
                    logger.info("Header -> {}: {}", headerName, httpRequest.getHeader(headerName))
            );
            logger.info("==================================================");
        }

        chain.doFilter(request, response);
    }
}
