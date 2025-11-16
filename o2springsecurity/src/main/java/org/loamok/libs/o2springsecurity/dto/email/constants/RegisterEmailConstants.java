package org.loamok.libs.o2springsecurity.dto.email.constants;

import lombok.NoArgsConstructor;

/**
 * Constantes des contenus de messages E-mails relatifs à l'inscription utilisateur
 *
 * @author Huby Franck
 */
@NoArgsConstructor
public final class RegisterEmailConstants {
    
    /**
     * Préfixe de clé des messages E-mails
     */
    public static final String EMAIL_MESSAGE_PREFIX = "REGISTER_EMAIL";
    
    /**
     * Titre de l'e-mail de validation d'adresse e-mail
     */
    public static final String REGISTER_EMAIL_VALIDATION_TITLE = """
            Validez votre inscription \u00e0 l'application Todo.""";
    /**
     * Corps de l'e-mail de validation d'adresse e-mail
     */
    public static final String REGISTER_EMAIL_VALIDATION = 
        """
        Vous avez cr\u00e9\u00e9 un profil sur l'application Todo.

        Afin de valider votre adresse e-mail merci de cliquer sur le lien suivant : %%%BASE_URL%%%/register/activate?key=%%%EMAIL_VERIFICATION_KEY%%%
        
        Notez que cette cl\u00e9 n'est valable que pendant 1 heure.

        Vous pouvez copier votre cl\u00e9 d'enregistrement manuellement dans le formulaire :
        \t- Cl\u00e9 d'enregistrement : %%%EMAIL_VERIFICATION_KEY%%%
        \t- Url d'activation : %%%BASE_URL%%%/register/activate

        Si vous n'avez pas ouvert de profil sur Todo vous pouvez nous le signaler.
        Pour cela utilisez l'url ci-dessous : 
        %%%BASE_URL%%%/register/deactivate?key=%%%EMAIL_VERIFICATION_KEY%%%

        """;
    
    /**
     * Titre de l'e-mail de désactivation d'adresse e-mail
     */
    public static final String REGISTER_EMAIL_INVALIDATION_TITLE = """
            Invalidation de la demande d'inscription \u00e0 l'application Todo.""";
    /**
     * Corps de l'e-mail de désactivation d'adresse e-mail
     */
    public static final String REGISTER_EMAIL_INVALIDATION = 
        """
        Vous avez demand\u00e9 l'invalidation d'un profil sur l'application Todo avec votre adresse e-mail.
        
        Ce message vous indique que votre demande a bien \u00e9t\u00e9 prise en compte.
        
        Si vous n'\u00e9tiez pas \u00e0 l'origine de cette demande nous vous prions de nous excuser pour la g\u00eane occasionn\u00e9e.
        
        Nous vous remercions pour votre vigilance.
        
        """;
    
    /**
     * Corps de l'e-mail "admin" de désactivation d'adresse e-mail
     */
    public static final String REGISTER_EMAIL_INVALIDATION_ADMIN = 
        """
        Bonjour,
        
        L'utilisateur %%%U_NAME%%% %%%U_FIRSTNAME%%% (%%%ORIGINAL_MAIL%%%) a demand\u00e9 l'annulation de sa souscription.
        
        """;
    
}
