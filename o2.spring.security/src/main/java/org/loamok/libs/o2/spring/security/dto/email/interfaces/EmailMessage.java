package org.loamok.libs.o2.spring.security.dto.email.interfaces;

import java.util.Map;

/**
 *
 * @author Huby Franck
 */
public interface EmailMessage {
    String getEmailMessage(String messageKey, Map<String, String> substitutions);
    String getEmailTitle(String titleKey, Map<String, String> substitutions);
}
