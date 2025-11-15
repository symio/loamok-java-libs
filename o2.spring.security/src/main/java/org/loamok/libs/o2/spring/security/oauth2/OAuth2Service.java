package org.loamok.libs.o2.spring.security.oauth2;

import java.util.Optional;
import org.loamok.libs.o2.spring.security.dto.response.OAuth2TokenResponse;

/**
 *
 * @author Huby Franck
 */
public interface OAuth2Service {
    Optional<OAuth2TokenResponse> generateClientCredentialsToken(String email, String clientSecret, String requestedScopes, String clientSignature);
}
