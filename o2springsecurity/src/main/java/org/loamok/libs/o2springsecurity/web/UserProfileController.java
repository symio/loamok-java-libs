package org.loamok.libs.o2springsecurity.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loamok.libs.o2springsecurity.dto.email.constants.RegisterEmailConstants;
import org.loamok.libs.o2springsecurity.dto.request.UserActivateRequest;
import org.loamok.libs.o2springsecurity.dto.request.UserEmailSearchRequest;
import org.loamok.libs.o2springsecurity.dto.request.UserNewPasswordAfterLostRequest;
import org.loamok.libs.o2springsecurity.entity.User;
import org.loamok.libs.o2springsecurity.manager.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur gestion des challenges de sécurité :
 * - activate : Active un utilisateur par validation de son adresse e-mail
 *      après l'auto-enregistrement d'un utilisateur
 * - deactivate : Annule l'activation d'un utilisateur
 *      après activate
 * - passwordLostStep1 : Première étape du challenge de sécurité pour mot de passe perdu
 * - step1Deactivate : Annule la première étape de réinitialisation de mot de passe d'un utilisateur
 *      après passwordLostStep1
 * - passwordLostStep2 : Seconde étape du challenge de sécurité pour mot de passe perdu.
 *      Changement effectif du mot de passe. 
 *          après passwordLostStep1
 *          pas après step1Deactivate
 * - step2Deactivate : Annule la seconde étape de réinitialisation de mot de passe d'un utilisateur
 *      après passwordLostStep2
 *      /!\ Attention ! : bloque l'utilisation du compte
 * 
 * @author Huby Franck
 */
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@Tag(name = "Register", description = "Opérations sécurisées sur la création de profils utilisateurs")
public class UserProfileController {

    /**
     * Journalisation
     */
    protected final Log logger = LogFactory.getLog(getClass());
    private final UserService userService;

    /**
     * activate: Active un utilisateur par validation de son adresse e-mail
     * 
     * @param userActivateRequest : Json contenant la clè relative à l'adresse e-mail de l'utilisateur à activer
     * @return Réponse json vide code 200 ou 400
     */
    @Operation(summary = "Active un utilisateur par validation de son adresse e-mail")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activation effectuée avec succès.",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Token manquant ou invalide",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    type = "object",
                    example = "{ \"error\": \"invalid_request\", \"error_description\": \"Token manquant ou invalide\" }"
                )
            )
        )
    })
    @PostMapping(value = "/activate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> activate(
        @Parameter(description = "Objet contenant la clé d'activation", required = true)
        @Valid @RequestBody UserActivateRequest userActivateRequest
    ) {
        try {
            Boolean verified = userService.activateRegisteredUser(userActivateRequest.getKey());
            if(verified.equals(Boolean.FALSE))
                return ResponseEntity.badRequest().build();
            
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * deactivate: Annule l'activation d'un utilisateur
     * 
     * @param userActivateRequest : Json contenant la clè relative à l'adresse e-mail de l'utilisateur à désactiver
     * @return Réponse json vide code 200 ou 400
     */
    @Operation(summary = "Annule l'activation d'un utilisateur")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activation annulée avec succès.",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Token manquant ou invalide",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    type = "object",
                    example = "{ \"error\": \"invalid_request\", \"error_description\": \"Token manquant ou invalide\" }"
                )
            )
        )
    })
    @PostMapping(value = "/deactivate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deactivate(
        @Parameter(description = "Objet contenant la clé d'activation", required = true)
        @Valid @RequestBody UserActivateRequest userActivateRequest
    ) {
        try {
            Boolean verified = userService.deactivateRegisteredUser(
                userActivateRequest.getKey(),  RegisterEmailConstants.EMAIL_MESSAGE_PREFIX +"_INVALIDATION"
            );
            
            if(verified.equals(Boolean.FALSE))
                return ResponseEntity.badRequest().build();
            
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * passwordLostStep1 : Première étape du challenge de sécurité pour mot de passe perdu
     * 
     * @param userSearchRequest : Json contenant l'adresse e-mail relative à l'utilisateur
     * @return Réponse json vide code 200
     */
    @Operation(summary = "Première étape du challenge de sécurité pour mot de passe perdu")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Première étape prise en compte.",
            content = @Content()
        )
    })
    @PostMapping(value = "/password-lost/step1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> passwordLostStep1(
        @Parameter(description = "Objet contenant l'adresse e-mail de l'utilisateur dont le mot de passe est perdu", required = true)
        @Valid @RequestBody UserEmailSearchRequest userSearchRequest
    ) {
        try {
            User u = userService.resetChallengeRegisteredUser1(userSearchRequest.getEmail());
            
            if(u == null)
                logUserChallengeError("Utilisateur non trouvé.");
        } catch (Exception e) {
            logUserChallengeError("Exception : " + e.toString());
        }
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * step1Deactivate : Annule la première étape de réinitialisation de mot de passe d'un utilisateur
     * 
     * @param userActivateRequest : Json contenant la clè relative à l'adresse e-mail de l'utilisateur
     * @return Réponse json vide code 200 ou 400
     */
    @Operation(summary = "Annule la première étape de réinitialisation de mot de passe d'un utilisateur")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Réinitialisation de mot de passe annulée avec succès.",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Token manquant ou invalide",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    type = "object",
                    example = "{ \"error\": \"invalid_request\", \"error_description\": \"Token manquant ou invalide\" }"
                )
            )
        )
    })
    @PostMapping(value = "/password-lost/step1/deactivate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> step1Deactivate(
        @Parameter(description = "Objet contenant le token pour la requête", required = true)
        @Valid @RequestBody UserActivateRequest userActivateRequest
    ) {
        try {
            Boolean verified = userService.disableResetChallenge1(userActivateRequest.getKey(), Boolean.TRUE);
            
            if(verified.equals(Boolean.FALSE))
                return ResponseEntity.badRequest().build();
            
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * passwordLostStep2 : Seconde étape du challenge de sécurité pour mot de passe perdu.
     *      Changement effectif du mot de passe. 
     * 
     * @param userNewPassword : Objet contenant la requête de changement de mot de passe de l'utilisateur
     * @return Réponse json vide code 200 ou 400
     */
    @Operation(summary = "Seconde étape du challenge de sécurité pour mot de passe perdu.\nChangement effectif du mot de passe.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Seconde étape prise en compte.",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Token manquant ou invalide ou mots de passes incorects",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    type = "object",
                    example = "{ \"error\": \"invalid_request\", \"error_description\": \"Token manquant ou invalide ou mots de passes incorects\" }"
                )
            )
        )
    })
    @PostMapping(value = "/password-lost/step2", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> passwordLostStep2(
        @Parameter(description = "Objet contenant la requête de changement de mot de passe de l'utilisateur", required = true)
        @Valid @RequestBody UserNewPasswordAfterLostRequest userNewPassword
    ) {
        try {
            Boolean result = userService.resetChallengeRegisteredUser2(
                    userNewPassword.getKey(), 
                    userNewPassword.getNewPassword(), 
                    userNewPassword.getPasswordConfirm()
            );
            
            if(result.equals(Boolean.FALSE)) {
                logUserChallengeError("Utilisateur non trouvé ou mot de passe incorrect.");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logUserChallengeError("Exception : " + e.toString());
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * step2Deactivate : Annule la seconde étape de réinitialisation de mot de passe d'un utilisateur
     *      après passwordLostStep2
     *      /!\ Attention ! : bloque l'utilisation du compte
     * 
     * @param userActivateRequest : Json contenant la clè relative à l'adresse e-mail de l'utilisateur
     * @return Réponse json vide code 200 ou 400
     */
    @Operation(summary = "Annule la seconde étape de réinitialisation de mot de passe d'un utilisateur")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Réinitialisation de mot de passe annulée avec succès.",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Token manquant ou invalide",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    type = "object",
                    example = "{ \"error\": \"invalid_request\", \"error_description\": \"Token manquant ou invalide\" }"
                )
            )
        )
    })
    @PostMapping(value = "/password-lost/step2/deactivate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> step2Deactivate(
        @Parameter(description = "Objet contenant le token pour la requête", required = true)
        @Valid @RequestBody UserActivateRequest userActivateRequest
    ) {
        try {
            Boolean verified = userService.disableResetChallenge2(userActivateRequest.getKey());
            
            if(verified.equals(Boolean.FALSE))
                return ResponseEntity.badRequest().build();
            
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private void logUserChallengeError(String message) {
        logger.info("### User Challenge error : ###");
        logger.info("Message : " + message);
    }
}
