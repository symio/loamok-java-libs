package org.loamok.libs.emailtemplates.exceptions;

/**
 * Exception personalisé pour l'envoi d'e-mails simples
 * 
 * @author Huby Franck
 */
public class EmailSendingException extends RuntimeException {
    /**
     * Constructeur
     * 
     * @param email adresse e-mail
     */
    public EmailSendingException(String email) {
        super("Error while Sending Mail to " + email);
    }
}
