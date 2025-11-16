package org.loamok.libs.o2springsecurity.exceptions;

/**
 * Exception personalisé pour un mot de passe non conforme
 *
 * @author Huby Franck
 */
public class InvalidPasswordException extends RuntimeException {
    /**
     * Constructeur
     */
    public InvalidPasswordException() {
        super("Password does not meet security requirements.");
    }
}
