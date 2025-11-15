package org.loamok.libs.o2.spring.security.manager;

import org.loamok.libs.o2.spring.security.entity.EmailDetails;


/**
 *
 * @author Huby Franck
 */
public interface EmailService {
    String getBaseurl();
    void setBaseurl(String baseurl);
    String sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
}
