package org.loamok.libs.o2springsecurity.oauth2;

import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.loamok.libs.o2springsecurity.config.LoamokSecurityProperties;
import org.loamok.libs.o2springsecurity.dto.response.OAuth2TokenResponse;
import org.loamok.libs.o2springsecurity.entity.User;
import org.loamok.libs.o2springsecurity.jwt.JwtService;
import org.loamok.libs.o2springsecurity.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation du service d'authentification OAuth2
 *
 * @author Huby Franck
 */
@Service
@AllArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {

    private final UserRepository uR;
    private final PasswordEncoder pE;
    private final JwtService jwtService;
    private final LoamokSecurityProperties securityProperties;

    private void cleanupUserTokens(User user) {
        if (user != null) {
            user.setAuthToken(null);
            user.setRememberMeToken(null);
            uR.save(user);
        }
    }
    
    private Set<String> parseScopeSet(String requestedScopes) {
        return Arrays.stream(requestedScopes.split("\\s+"))
                .map(String::trim)
                .collect(Collectors.toSet());
    }
    
    private boolean isValidBasicAuth(User user, String clientSecret) {
        return user != null
                && pE.matches(clientSecret, user.getPassword())
                && user.isAccountNonExpired() 
                && user.isAccountNonLocked() 
                && user.isEnabled();
    }
    
    private boolean isUserAccountValid(User user) {
        return user.isAccountNonExpired() && user.isAccountNonLocked() && user.isEnabled();
    }

    private boolean isValidRefreshAuth(User user, String clientSecret, String email, String clientSignature) {
        if (!jwtService.isClientCredentialsTokenValid(clientSecret, email)
                || user == null
                || user.getRememberMeToken() == null
                || !isUserAccountValid(user)) {
            return false;
        }

        Claims refreshedClaims = jwtService.extractAllClaimsForced(user.getAuthToken());
        return clientSignature.equals(refreshedClaims.get("client-signature"));
    }

    private boolean isValidRememberedAuth(Set<String> scopeSet, User user, String clientSecret, String email, String clientSignature) {
        scopeSet.add("rememberme");
        
        if (!jwtService.isClientCredentialsTokenValid(clientSecret, email)
                || user == null
                || user.getRememberMeToken() == null
                || !isUserAccountValid(user)) {
            return false;
        }

        Claims rememberedClaims = jwtService.extractAllClaims(user.getRememberMeToken());
        return clientSignature.equals(rememberedClaims.get("client-signature"));
    }
    
    private boolean isValidAuthentication(Set<String> scopeSet, User user, String clientSecret, String email, String clientSignature) {
        if (!scopeSet.contains("remembered") && !scopeSet.contains("refresh")) {
            return isValidBasicAuth(user, clientSecret);
        }

        if (scopeSet.contains("refresh")) {
            return isValidRefreshAuth(user, clientSecret, email, clientSignature);
        }

        if (scopeSet.contains("remembered")) {
            return isValidRememberedAuth(scopeSet, user, clientSecret, email, clientSignature);
        }

        return false;
    }

    private int calculateExpirationHours(Set<String> scopeSet, String clientSecret) {
        if (scopeSet.contains("refresh")) {
            Date originalExpiration = jwtService.extractExpiration(clientSecret);
            long currentTimeMillis = System.currentTimeMillis();
            long remainingTimeMillis = originalExpiration.getTime() - currentTimeMillis;
            
            // Utilise la config pour le minimum, au lieu de 2 en dur
            int minHours = securityProperties.getJwt().getStoredTokenExpirationHours();
            return Math.max(minHours, (int) (remainingTimeMillis / (1000 * 60 * 60))); 
        }
        
        // Utilise la config au lieu de 24 en dur
        return securityProperties.getJwt().getAccessTokenExpirationHours(); 
    }
    
    private String generateRememberMeTokenIfNeeded(Set<String> scopeSet, Map<String, Object> claims, User user, String email, int expirationHours) {
        if (!scopeSet.contains("rememberme")) {
            return null;
        }

        Map<String, Object> claimsRemember = new HashMap<>();
        claims.remove("scope");
        claims.put("scope", "rememberme");
        claimsRemember.putAll(claims);
        claimsRemember.remove("client-signature");
        
        // Utilise la config pour calculer la duree en heures au lieu de expirationHours * 365
        int rememberMeDays = securityProperties.getJwt().getRememberMeTokenExpirationDays();
        int rememberMeHours = rememberMeDays * 24;
        
        final String storedRememberToken = jwtService.generateClientCredentialsToken(claims, email, rememberMeHours);
        user.setRememberMeToken(storedRememberToken);
        
        return jwtService.generateClientCredentialsToken(claimsRemember, email, rememberMeHours);
    }
    
    private Optional<OAuth2TokenResponse> generateTokens(Set<String> scopeSet, User user, String email, String clientSecret, String clientSignature) {
        String scope = "access";
        
        Map<String, Object> claims = genClaimsForToken(email, scope, clientSignature, "client_credentials", user);
        Map<String, Object> claimsAuth = new HashMap<>();

        claimsAuth.putAll(claims);
        claimsAuth.remove("client-signature");

        // Utilise la config pour le token stocke au lieu de 2 en dur
        int storedTokenHours = securityProperties.getJwt().getStoredTokenExpirationHours();
        final String storedToken = jwtService.generateClientCredentialsToken(claims, email, storedTokenHours);
        user.setAuthToken(storedToken);

        claimsAuth.put("originally_expires", jwtService.extractExpiration(storedToken));

        int expirationHours = calculateExpirationHours(scopeSet, clientSecret);

        final String authToken = jwtService.generateClientCredentialsToken(claimsAuth, email, expirationHours);

        String rememberMeToken = generateRememberMeTokenIfNeeded(scopeSet, claims, user, email, expirationHours);

        uR.save(user);

        return Optional.of(new OAuth2TokenResponse(authToken, rememberMeToken, "Bearer", 
                expirationHours * 60 * 60, jwtService.extractExpiration(storedToken).getTime(), scope));
    }
    
    @Override
    public Optional<OAuth2TokenResponse> generateClientCredentialsToken(String email, String clientSecret, String requestedScopes, String clientSignature) {
        Set<String> scopeSet = parseScopeSet(requestedScopes);
        User user = uR.findByEmail(email);

        if (scopeSet.contains("cleanup")) {
            cleanupUserTokens(user);
            return Optional.empty();
        }

        if (!isValidAuthentication(scopeSet, user, clientSecret, email, clientSignature)) {
            cleanupUserTokens(user);
            return Optional.empty();
        }

        return generateTokens(scopeSet, user, email, clientSecret, clientSignature);
    }

    private Map<String, Object> genClaimsForToken(String email, String scope, String clientSignature, String tokenType, User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("client_id", email);
        claims.put("display_name", user.getFirstname() + " " + user.getName());
        claims.put("scope", scope);
        claims.put("token_type", tokenType);
        claims.put("authority", user.getAuthority());
        claims.put("isAdmin", user.getRole().getIsAdmin());
        claims.put("client-signature", clientSignature);

        return claims;
    }
}
