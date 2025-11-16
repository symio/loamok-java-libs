package org.loamok.libs.o2springsecurity.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Génère la signature du client utilisé pour intéroger l'api
 *
 * @author Huby Franck
 */
public interface ClientSignatureUtil {
    /**
     * Génère une signature unique
     * 
     * @param request requête http
     * @return La signature
     */
    String buildClientSignature(HttpServletRequest request);
    /**
     * hash la signature
     * 
     * @param source la signature à hasher
     * @return la signature hashée
     */
    String buildHashedSignature(String source);
}
