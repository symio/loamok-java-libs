package org.loamok.libs.o2.spring.security.jwt;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Map;

/**
 * Définition du service JWT
 * (Json Web Token)
 *
 * @author Huby Franck
 */
public interface JwtService {
    /**
     * Extrait les claims d'un token
     * 
     * @param token token OAuth 2
     * @return claims
     */
    Claims extractAllClaims(String token);
    /**
     * Extrait les claims d'un token même expiré
     * 
     * @param token token OAuth 2
     * @return claims
     */
    Claims extractAllClaimsForced(String token);
    /**
     * Extrait l'autorité originale du token OAuth 2
     * 
     * @param token token OAuth 2
     * @return l'autorité original du token OAuth2
     */
    String extractAuthority(String token);
    /**
     * Extrait la date d'expiration du token
     * 
     * @param token token OAuth 2
     * @return expiration Datetime
     */
    Date extractExpiration(String token);
    /**
     * Extrait les scopes
     * 
     * @param token token OAuth 2
     * @return les scopes OAuth2
     */
    String extractScopes(String token);
    /**
     * Extrait le pseudonyme (email) de l'utilisateur
     * 
     * @param token token OAuth 2
     * @return le pseudonyme de l'utilisateur
     */
    String extractUserName(String token);
    /**
     * Génère une représentation textuelle d'un token
     * Client Credentials OAuth2
     * 
     * @param claims collection de claims pour ce token
     * @param email pseudonyme de l'utilisateur
     * @param hours durée de validitée en heures
     * @return chaîne de caractères représentant le token ClientCredentials
     */
    String generateClientCredentialsToken(Map<String, Object> claims, String email, int hours);
    /**
     * Vérifie si le token est bien OAuth2 Client Credentials 
     * 
     * @param token token OAuth 2
     * @return true si c'est un token OAuth2 Client Credentials
     */
    boolean isClientCredentialsToken(String token);
    /**
     * Vérifie que le token soit valide
     * 
     * @param token token OAuth 2
     * @param email pseudonyme de l'utilisateur
     * @return true si un token client credentials est valide pour cet email
     */
    boolean isClientCredentialsTokenValid(String token, String email);
}
