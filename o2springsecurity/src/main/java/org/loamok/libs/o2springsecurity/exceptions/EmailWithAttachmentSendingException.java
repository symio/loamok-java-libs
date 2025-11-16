package org.loamok.libs.o2springsecurity.exceptions;

/**
 * Exception personalisé pour l'envoi d'e-mails avec pièce jointes
 *
 * @author Huby Franck
 */
public class EmailWithAttachmentSendingException extends RuntimeException {
    /**
     * Constructeur
     * 
     * @param email adresse e-mail
     */
    public EmailWithAttachmentSendingException(String email) {
        super("Error while Sending Mail with attachent to " + email);
    }
}
