package org.loamok.libs.o2springsecurity.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Requête de vérification de mot de passe
 *
 * @author Huby Franck
 */
@Schema(description = "Requête de vérification de mot de passe")
@Data
public class CheckPasswordRequest {
    @Schema(description = "Nouveau mot de passe", example = "@Bcd1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String newPassword;
    @Schema(description = "Confirmation du nouveau mot de passe", example = "@Bcd1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String passwordConfirm;
}
