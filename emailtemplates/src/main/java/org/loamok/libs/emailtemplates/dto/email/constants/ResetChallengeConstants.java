package org.loamok.libs.emailtemplates.dto.email.constants;

import lombok.NoArgsConstructor;

/**
 * Constantes des contenus de messages E-mails relatifs au challenge mot de passe perdu
 * 
 * @author Huby Franck
 */
@NoArgsConstructor
public final class ResetChallengeConstants {
    /**
     * Préfixe de clé des messages E-mails
     */
    public static final String EMAIL_MESSAGE_PREFIX = "RESET_CHALLENGE";

    /**
     * Titre de l'e-mail de validation de la première étape
     */
    public static final String RESET_CHALLENGE_KEY_GENERATION_TITLE = """
        R\u00e9initialiser votre mot de passe d'acc\u00e8s \u00e0 l'application Todo.""";
    /**
     * Corps de l'e-mail de validation de la première étape
     */
    public static final String RESET_CHALLENGE_KEY_GENERATION = 
        """
        Vous avez demand\u00e9 la r\u00e9initialisation de votre mot de passe pour l'application Todo.

        Afin de proc\u00e9der \u00e0 cette r\u00e9initialisation merci de cliquer sur le lien suivant : 
        %%%BASE_URL%%%/register/password-lost2?key=%%%EMAIL_VERIFICATION_KEY%%%
        
        Notez que cette cl\u00e9 n'est valable que pendant 1 heure.

        Vous pouvez copier votre cl\u00e9 d'activation manuellement dans le formulaire :
        \t- Cl\u00e9 d'activation : %%%EMAIL_VERIFICATION_KEY%%%
        \t- Url d'activation : %%%BASE_URL%%%/register/password-lost2

        Si vous n'avez pas demand\u00e9 cette r\u00e9initialisation sur Todo vous pouvez nous le signaler.
        Pour cela utilisez l'url ci-dessous : 
        %%%BASE_URL%%%/register/password-lost1/deactivate?key=%%%EMAIL_VERIFICATION_KEY%%%

        Vous pouvez copier votre cl\u00e9 d'activation manuellement dans le formulaire :
        \t- Cl\u00e9 d'activation : %%%EMAIL_VERIFICATION_KEY%%%
        \t- Url d'annulation : %%%BASE_URL%%%/register/password-lost1/deactivate

        """;
    
    /**
     * Titre de l'e-mail d'annulation de la première étape
     */
    public static final String RESET_CHALLENGE_INVALIDATION_TITLE = """
        Invalidation de la demande de r\u00e9initialisation de mot de passe.""";
    /**
     * Corps de l'e-mail d'annulation de la première étape
     */
    public static final String RESET_CHALLENGE_INVALIDATION = 
        """
        Vous avez demand\u00e9 l'invalidation d'une demande de r\u00e9initialisation de mot de passe sur l'application Todo avec votre adresse e-mail.
        
        Ce message vous indique que votre demande a bien \u00e9t\u00e9 prise en compte.
        
        Si vous n'\u00e9tiez pas \u00e0 l'origine de cette demande, nous vous prions de nous excuser pour la g\u00eane occasionn\u00e9e.
        
        Nous vous remercions pour votre vigilance.
        
        """;
    
    /**
     * Corps de l'e-mail "Admin" d'annulation de la première étape
     */
    public static final String RESET_CHALLENGE_INVALIDATION_ADMIN = 
        """
        Bonjour,
        
        L'utilisateur %%%U_NAME%%% %%%U_FIRSTNAME%%% (%%%ORIGINAL_MAIL%%%) a demand\u00e9 l'annulation de sa r\u00e9initialisation de mot de passe.
        
        """;
    
    /**
     * Titre de l'e-mail de validation de la seconde étape
     */
    public static final String RESET_CHALLENGE_VALIDATION_TITLE = """
        Demande de r\u00e9initialisation de mot de passe valid\u00e9e.""";
    /**
     * Corps de l'e-mail de validation de la seconde étape
     */
    public static final String RESET_CHALLENGE_VALIDATION = 
        """
        Vous avez demand\u00e9 la r\u00e9initialisation de votre mot de passe pour l'application Todo.

        Votre mot de passe a bien \u00e9t\u00e9 modifi\u00e9.

        Si vous n'avez pas demand\u00e9 cette r\u00e9initialisation sur Todo vous pouvez nous le signaler.
        Pour cela utilisez l'url ci-dessous : 
        %%%BASE_URL%%%/register/password-lost2/deactivate?key=%%%EMAIL_VERIFICATION_KEY%%%
        
        Vous pouvez copier votre cl\u00e9 d'activation manuellement dans le formulaire :
        \t- Cl\u00e9 d'activation : %%%EMAIL_VERIFICATION_KEY%%%
        \t- Url d'annulation : %%%BASE_URL%%%/register/password-lost2/deactivate

        """;
    
    /**
     * Titre de l'e-mail d'annulation de la seconde étape
     */
    public static final String RESET_CHALLENGE_DISABLING_TITLE = """
        D\u00e9sactivation de votre compte.""";
    /**
     * Corps de l'e-mail d'annulation de la seconde étape
     */
    public static final String RESET_CHALLENGE_DISABLING = 
        """
        Vous avez demand\u00e9 l'annulation d'une demande de r\u00e9initialisation de mot de passe d\u00e9j\u00e0 appliqu\u00e9e avec votre adresse e-mail.
        
        Ce message vous indique que votre demande a bien \u00e9t\u00e9 prise en compte.
        
        Votre compte a \u00e9t\u00e9 d\u00e9sactiv\u00e9 par mesure de s\u00e9curit\u00e9.
        Veuillez prendre contact avec nous afin que nous puissions vous d\u00e9bloquer l'acc\u00e8s \u00e0 votre compte.
        
        Nous vous remercions pour votre vigilance.
        
        """;
    
    /**
     * Corps de l'e-mail "admin" d'annulation de la seconde étape
     */
    public static final String RESET_CHALLENGE_DISABLING_ADMIN = 
        """
        Bonjour,
        
        L'utilisateur %%%U_NAME%%% %%%U_FIRSTNAME%%% (%%%ORIGINAL_MAIL%%%) a demand\u00e9 l'annulation de sa r\u00e9initialisation de mot de passe.
        Son compte a \u00e9t\u00e9 d\u00e9sactiv\u00e9.
        
        """;
    
}
