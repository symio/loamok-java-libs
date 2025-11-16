package org.loamok.libs.o2springsecurity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTo privé pour le DTO de réponse avec tokens voir TokenResponse
 *
 * @author Huby Franck
 */
@Schema(description = "Réponse contenant le token d'accès OAuth2")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("remember_me_token")
    private String rememberMeToken;
    
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("expires_in")
    private Integer expiresIn;
    
    @JsonProperty("originally_expires")
    private Long originallyExpires;
    
    private String scope;
}
