package org.loamok.libs.o2springsecurity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Json de réponse contenant le résultat d'une validation de mot de passe
 *
 * @author Huby Franck
 */
@Schema(description = "Réponse contenant le résultat d'une validation de mot de passe")
@Data
public class CheckPasswordsResponse {
    @Schema(description = "Clé de validation de mot de passe", example = "V66ogxJWkUYWPjfVJSsunNbEwpA3aTRTBRBzmDgbk7Q")
    private String passwordKey;
}
