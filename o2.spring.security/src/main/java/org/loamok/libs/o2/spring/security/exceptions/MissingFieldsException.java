package org.loamok.libs.o2.spring.security.exceptions;

/**
 *
 * @author Huby Franck
 */
public class MissingFieldsException extends RuntimeException {
    public MissingFieldsException(String field) {
        super("Missing required field: " + field);
    }
}
