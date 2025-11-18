package org.loamok.libs.emailtemplates.dto.email.interfaces;

import java.util.Map;

/**
 * Définition d'un message E-mail
 *
 * @author Huby Franck
 */
public interface EmailMessage {
    /**
     * Retourne le corps d'un message e-mail
     * 
     * @param messageKey clé d'identification du message
     * @param substitutions map de variables de substitutions
     * @return le corps du message prêt à envoyer
     */
    String getEmailMessage(String messageKey, Map<String, String> substitutions);
    /**
     * Retourne le titre d'un message e-mail
     * 
     * @param titleKey clé d'identification du message
     * @param substitutions map de variables de substitutions
     * @return le titre du message prêt à envoyer
     */
    String getEmailTitle(String titleKey, Map<String, String> substitutions);
}
