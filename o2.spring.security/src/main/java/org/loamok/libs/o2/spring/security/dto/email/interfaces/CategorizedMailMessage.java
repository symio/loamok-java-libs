package org.loamok.libs.o2.spring.security.dto.email.interfaces;

/**
 *
 * @author Huby Franck
 */
public interface CategorizedMailMessage {
    String getEmailMessage(String messageKey);
    String getEmailTitle(String titleKey);
}
