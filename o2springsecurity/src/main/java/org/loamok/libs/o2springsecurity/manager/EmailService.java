package org.loamok.libs.o2springsecurity.manager;

import org.loamok.libs.o2springsecurity.entity.EmailDetails;


/**
 * Définition du service d'envoi d'e-mails
 *
 * @author Huby Franck
 */
public interface EmailService {
    /**
     * Url de base du front (getter)
     * 
     * @return une url
     */
    String getBaseurl();
    /**
     * Url de base du front (setter)
     * 
     * @param baseurl une url
     */
    void setBaseurl(String baseurl);
    /**
     * Envoi un e-mail simple
     * 
     * @param details paramètres de l'email
     * @return le status d'envoi
     */
    String sendSimpleMail(EmailDetails details);
    /**
     * Envoi un e-mail avec pièce jointe
     * 
     * @param details paramètres de l'email
     * @return le status d'envoi
     */
    String sendMailWithAttachment(EmailDetails details);
}
