package org.loamok.libs.o2springsecurity.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Requête de validation d'utilisateur
 *
 * @author Huby Franck
 */
@Schema(description = "Requête de validation d'utilisateur")
@Data
public class UserActivateRequest {
    @Schema(description = "Clé de validation E-mail", example = "E0QXcvxuebajVfYezfYYbJEbbwAa2elB8eiqFBbI/Lk=", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String key;
}
