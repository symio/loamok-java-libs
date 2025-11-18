package org.loamok.libs.emailtemplates.exceptions;

/**
 * Exception personalisé pour la réutilisation d'une adresse e-mail déjà utilisée
 *
 * @author Huby Franck
 */
public class EmailAlreadyExistsException extends RuntimeException {
    /**
     * Constructeur
     * 
     * @param email adresse e-mail
     */
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}
