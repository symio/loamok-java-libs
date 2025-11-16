package org.loamok.libs.o2springsecurity.dto.email.interfaces;

/**
 * Définition des messages e-mail catégorisés
 *
 * @author Huby Franck
 */
public interface CategorizedMailMessage {
    /**
     * Retourne le corps d'un message e-mail
     * 
     * @param messageKey clé d'identification du message
     * @return le corps du message
     */
    String getEmailMessage(String messageKey);
    /**
     * Retourne le titre d'un message e-mail
     * 
     * @param titleKey clé d'identification du message
     * @return le titre du message
     */
    String getEmailTitle(String titleKey);
}
