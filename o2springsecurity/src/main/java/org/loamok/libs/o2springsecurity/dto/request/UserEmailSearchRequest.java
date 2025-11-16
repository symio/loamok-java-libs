package org.loamok.libs.o2springsecurity.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Requête de recherche d'utilisateur par adresse e-mail
 * 
 * @author Huby Franck
 */
@Schema(description = "Requête de recherche d'utilisateur")
@Data
public class UserEmailSearchRequest {
    @Schema(description = "Adresse E-mail", example = "wille.e.coyote@acme.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    private String email;
}
