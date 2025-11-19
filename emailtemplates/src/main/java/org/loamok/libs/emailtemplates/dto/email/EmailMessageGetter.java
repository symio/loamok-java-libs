package org.loamok.libs.emailtemplates.dto.email;

import java.util.Map;
import org.loamok.libs.emailtemplates.dto.email.constants.EmailMessageConstants;
import org.loamok.libs.emailtemplates.dto.email.constants.RegisterEmailConstants;
import org.loamok.libs.emailtemplates.dto.email.constants.ResetChallengeConstants;
import org.loamok.libs.emailtemplates.dto.email.interfaces.EmailMessage;
import org.loamok.libs.emailtemplates.dto.email.interfaces.RegisterEmail;
import org.loamok.libs.emailtemplates.dto.email.interfaces.ResetChallenge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implémentation des messages e-mail
 *
 * @author Huby Franck
 */
@Service
public class EmailMessageGetter implements EmailMessage {
    @Autowired
    private RegisterEmail registerMailMessages;
    @Autowired
    private ResetChallenge resetChallengeMessages;
    
    private static final String SEPARATOR = EmailMessageConstants.EMAIL_MESSAGE_SUBSTITUTION_SEPARATOR;
    
    @Value("${loamok.emails.email.application-name}")
    private String APP_NAME;
    
    @Override
    public String getEmailMessage(String messageKey, Map<String, String> substitutions) {
        StringBuilder resultingMessage = new StringBuilder();
        
        if(!messageKey.endsWith(EmailMessageConstants.EMAIL_ADMIN_SUFFIX))
            resultingMessage.append(EmailMessageConstants.EMAIL_MESSAGE_STARTING);
        
        if(messageKey.startsWith(RegisterEmailConstants.EMAIL_MESSAGE_PREFIX)) {
            resultingMessage.append(registerMailMessages.getEmailMessage(messageKey));
        } else if (messageKey.startsWith(ResetChallengeConstants.EMAIL_MESSAGE_PREFIX)) {
            resultingMessage.append(resetChallengeMessages.getEmailMessage(messageKey));
        }
        
        resultingMessage.append(EmailMessageConstants.EMAIL_MESSAGE_ENDING);
        
        String message = resultingMessage.toString();
        
        for (String key : substitutions.keySet()) {
            message = message.replace(SEPARATOR + key + SEPARATOR, substitutions.get(key));
        }
        
        message = message.replace(SEPARATOR + "APP_NAME" + SEPARATOR, APP_NAME);
        
        return message;
    }

    @Override
    public String getEmailTitle(String titleKey, Map<String, String> substitutions) {
        StringBuilder resultingTitle = new StringBuilder();
        
        if(titleKey.startsWith(RegisterEmailConstants.EMAIL_MESSAGE_PREFIX)) {
            resultingTitle.append(registerMailMessages.getEmailTitle(titleKey));
        } else if (titleKey.startsWith(ResetChallengeConstants.EMAIL_MESSAGE_PREFIX)) {
            resultingTitle.append(resetChallengeMessages.getEmailTitle(titleKey));
        }
        
        String title = resultingTitle.toString();
        
        if(substitutions != null) {
            for (String key : substitutions.keySet()) {
                title = title.replace(SEPARATOR + key + SEPARATOR, substitutions.get(key));
            }
        }
        title = title.replace(SEPARATOR + "APP_NAME" + SEPARATOR, APP_NAME);
        
        return title;
    }
    
}
