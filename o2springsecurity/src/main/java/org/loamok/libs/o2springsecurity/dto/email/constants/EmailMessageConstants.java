package org.loamok.libs.o2springsecurity.dto.email.constants;

import lombok.NoArgsConstructor;

/**
 * Constantes générales pour les E-mails
 *
 * @author Huby Franck
 */
@NoArgsConstructor
public final class EmailMessageConstants {
    /**
     * Suffixe de clé pour les messages E-mail à destination des administrateurs
     */
    public static final String EMAIL_ADMIN_SUFFIX = "_ADMIN";
    /**
     * Chaîne de caractères utilisée comme séparateur de variables
     * Par exemple %%%U_NAME%%% :
     *  Variable : U_NAME
     *  Séparateurs : %%%
     */
    public static final String EMAIL_MESSAGE_SUBSTITUTION_SEPARATOR = "%%%";
    /**
     * Introduction des messages E-mail
     */
    public static final String EMAIL_MESSAGE_STARTING = """
        Bonjour %%%U_NAME%%% %%%U_FIRSTNAME%%%,

        """;
    /**
     * Fin des messages E-mail
     */
    public static final String EMAIL_MESSAGE_ENDING = 
        """
        Cordialement,

        --,
        L'\u00e9quipe de Todo
        %%%BASE_URL%%%/mentions-legales 
        """;
    
}
