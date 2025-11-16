package org.loamok.libs.o2springsecurity.oauth2;

import java.util.Optional;
import org.loamok.libs.o2springsecurity.dto.response.OAuth2TokenResponse;

/**
 * Définition du service d'authentification Oauth2
 *
 * @author Huby Franck
 */
public interface OAuth2Service {
    /**
     * Génére des crédentials JWT Oauth2
     * 
     * @param email Username
     * @param clientSecret Mot de passe
     * @param requestedScopes Scopes demandés
     * @param clientSignature Signature du client utilisé
     * @return Oauth2TokenResponse
     */
    Optional<OAuth2TokenResponse> generateClientCredentialsToken(String email, String clientSecret, String requestedScopes, String clientSignature);
}
