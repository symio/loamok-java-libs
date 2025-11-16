package org.loamok.libs.o2springsecurity.exceptions;

/**
 * Exception personnalisées pour champs requis non renseignés
 *
 * @author Huby Franck
 */
public class MissingFieldsException extends RuntimeException {
    /**
     * Constructeur
     * 
     * @param field Nom du champ en défaut
     */
    public MissingFieldsException(String field) {
        super("Missing required field: " + field);
    }
}
