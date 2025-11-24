package org.loamok.libs.o2springsecurity.manager;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loamok.libs.emailtemplates.config.LoamokEmailTemplatesProperties;
import org.loamok.libs.emailtemplates.dto.email.constants.EmailMessageConstants;
import org.loamok.libs.emailtemplates.dto.email.constants.RegisterEmailConstants;
import org.loamok.libs.emailtemplates.dto.email.constants.ResetChallengeConstants;
import org.loamok.libs.emailtemplates.dto.email.interfaces.EmailMessage;
import org.loamok.libs.emailtemplates.manager.EmailService;
import org.loamok.libs.o2springsecurity.config.LoamokSecurityProperties;
import org.loamok.libs.emailtemplates.entity.EmailDetails;
import org.loamok.libs.o2springsecurity.entity.Role;
import org.loamok.libs.o2springsecurity.entity.User;
import org.loamok.libs.o2springsecurity.exceptions.EmailAlreadyExistsException;
import org.loamok.libs.o2springsecurity.exceptions.EmailSendingException;
import org.loamok.libs.o2springsecurity.exceptions.InvalidPasswordException;
import org.loamok.libs.o2springsecurity.exceptions.MissingFieldsException;
import org.loamok.libs.o2springsecurity.repository.RoleRepository;
import org.loamok.libs.o2springsecurity.repository.UserRepository;
import org.loamok.libs.o2springsecurity.util.ClientSignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implémentation du manager d'utilisateur(s)
 *
 * @author Huby Franck
 */
@Service
@RequiredArgsConstructor
public class UserManager implements UserService {

    /**
     * Journalisation
     */
    protected final Log logger = LogFactory.getLog(getClass());
    // Regex pour valider le mot de passe
    private static final String PASSWORD_PATTERN
            = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_\\-|~#])[A-Za-z\\d@$!%*?&_\\-|~#]{8,}$";
    private static final Pattern PATTERN = Pattern.compile(PASSWORD_PATTERN);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Value("${loamok.security.jwt.secret}")
    private String SECRET_KEY;

    private final EmailMessage messageGetter;
    private final UserRepository uR;
    private final RoleRepository rR;
    private final ClientSignatureUtil csb;
    private final EmailService emailManager;
    private final LoamokSecurityProperties securityProperties;
    private final LoamokEmailTemplatesProperties emailProperties;

    @Override
    @Transactional
    public User resetChallengeRegisteredUser1(String email) {
        User u = uR.findByEmail(email);

        if (u == null) {
            return u;
        }

        if (u.getEmailVerificationKey() != null && u.getKeyValidity() != null) {
            return u;
        }

        Map<String, String> substitutions = new HashMap<>();
        substitutions.put("BASE_URL", emailManager.getBaseurl());
        substitutions.put("U_NAME", u.getName());
        substitutions.put("U_FIRSTNAME", u.getFirstname());

        String message = messageGetter.getEmailMessage(ResetChallengeConstants.EMAIL_MESSAGE_PREFIX + "_KEY_GENERATION", substitutions);

        resetKeyEmail(u, Boolean.FALSE);
        String status = generateAndSetEmailVerificationKey(
                u,
                messageGetter.getEmailTitle(ResetChallengeConstants.EMAIL_MESSAGE_PREFIX + "_KEY_GENERATION_TITLE", null),
                message
        );

        uR.save(u);
        logger.info("Registering security challenge for User " + u.getEmail() + " with " + u.getEmailVerificationKey() + " Email Key.");
        logger.info("Email sent with " + status);

        return u;
    }

    @Override
    @Transactional
    public Boolean disableResetChallenge1(String emailKey, Boolean resetOnly) {
        if (Objects.equals(resetOnly, Boolean.FALSE)) {
            return deactivateRegisteredUser(emailKey, RegisterEmailConstants.EMAIL_MESSAGE_PREFIX + "_INVALIDATION");
        }

        User u = getUserByEmailKey(emailKey);

        if (u != null && u.getEmailVerificationKey() != null && u.getEmailVerificationKey().equals(emailKey)) {
            if (Objects.equals(deactivateRegisteredUser(emailKey, ResetChallengeConstants.EMAIL_MESSAGE_PREFIX + "_INVALIDATION"), Boolean.TRUE)) {
                u.setEnabled(Boolean.TRUE);
                u.setEmail(u.getEmail().replace("disabled_", ""));

                uR.save(u);
            } else {
                return Boolean.FALSE;
            }

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    @Transactional
    public Boolean resetChallengeRegisteredUser2(String emailKey, String newPassword, String passwordConfirm) {
        User u = getUserByEmailKey(emailKey);

        if (u == null) {
            return Boolean.FALSE;
        }

        if (u.getEmailVerificationKey() == null || !u.getEmailVerificationKey().equals(emailKey)) {
            resetKeyEmail(u, Boolean.FALSE);
            uR.save(u);

            return Boolean.FALSE;
        }
        if (u.getEnabled().equals(Boolean.TRUE)) {
            resetKeyEmail(u, Boolean.TRUE);
            uR.save(u);

            return Boolean.FALSE;
        }

        if (Duration.between(u.getKeyValidity(), Instant.now()).toMillis() > 0) {
            resetKeyEmail(u, Boolean.FALSE);
            uR.save(u);

            return Boolean.FALSE;
        }

        if (newPassword.equals(passwordConfirm)) {
            if (!checkPasswordCorrect(newPassword)) {
                return Boolean.FALSE;
            }

            resetKeyEmail(u, Boolean.TRUE);

            String storedPassword = u.getPassword();
            String newPasswordEncoded = passwordEncoder.encode(newPassword);

            if (storedPassword.startsWith("{bcrypt}")) {
                storedPassword = storedPassword.substring(8); 
            }
            
            if (passwordEncoder.matches(newPasswordEncoded, storedPassword)) {
                resetKeyEmail(u, Boolean.FALSE);
                uR.save(u);
                return Boolean.FALSE;
            }
            
            u.setPassword("{bcrypt}" + passwordEncoder.encode(newPassword));
            
            Map<String, String> substitutions = new HashMap<>();
            substitutions.put("BASE_URL", emailManager.getBaseurl());
            substitutions.put("U_NAME", u.getName());
            substitutions.put("U_FIRSTNAME", u.getFirstname());

            String urlSafeSignature = generateKey(u);
            u.setEmailVerificationKey(urlSafeSignature);
            u.setKeyValidity(null);

            String message = messageGetter.getEmailMessage(ResetChallengeConstants.EMAIL_MESSAGE_PREFIX + "_VALIDATION", substitutions);

            String separator = EmailMessageConstants.EMAIL_MESSAGE_SUBSTITUTION_SEPARATOR;
            message = message.replace(separator + "EMAIL_VERIFICATION_KEY" + separator, u.getEmailVerificationKey());

            String status = emailManager.sendSimpleMail(EmailDetails.builder()
                    .recipient(u.getEmail())
                    .subject(messageGetter.getEmailTitle(ResetChallengeConstants.EMAIL_MESSAGE_PREFIX + "_VALIDATION_TITLE", null))
                    .msgBody(message)
                    .build());

            uR.save(u);

            logger.info("Registering security challenge 2 for User " + u.getEmail() + " with " + u.getEmailVerificationKey() + " Email Key.");
            logger.info("Email sent with " + status);
        } else {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public Boolean disableResetChallenge2(String emailKey) {
        User u = getUserByEmailKey(emailKey);

        if (u != null && u.getEmailVerificationKey() != null && u.getEmailVerificationKey().equals(emailKey) && u.getKeyValidity() == null) {
            resetKeyEmail(u, Boolean.FALSE);
            String originalMail = u.getEmail();
            u.setEmail("security_disabled_" + u.getEmail());
            uR.save(u);

            try {
                String title = null;
                Map<String, String> substitutions = new HashMap<>();
                substitutions.put("BASE_URL", emailManager.getBaseurl());
                substitutions.put("U_NAME", u.getName());
                substitutions.put("U_FIRSTNAME", u.getFirstname());

                title = messageGetter.getEmailTitle(ResetChallengeConstants.EMAIL_MESSAGE_PREFIX + "_DISABLING_TITLE", null);

                String message = messageGetter.getEmailMessage(ResetChallengeConstants.EMAIL_MESSAGE_PREFIX + "_DISABLING", substitutions);
                // send email to user
                String status = emailManager.sendSimpleMail(EmailDetails.builder()
                        .recipient(originalMail)
                        .subject(title)
                        .msgBody(message)
                        .build());
                logger.info("Email sent to user with " + status);

                // send Email to admin
                substitutions.put("ORIGINAL_MAIL", originalMail);
                String messageAdmin = messageGetter.getEmailMessage(
                        ResetChallengeConstants.EMAIL_MESSAGE_PREFIX + "_DISABLING" + EmailMessageConstants.EMAIL_ADMIN_SUFFIX,
                        substitutions
                );
                String statusAdmin = emailManager.sendSimpleMail(EmailDetails.builder()
                        .recipient(emailProperties.getEmail().getAdminEmail())
                        .subject(title)
                        .msgBody(messageAdmin)
                        .build());

                logger.info("Email sent to admin with " + statusAdmin);
            } catch (EmailSendingException e) {
                logger.info("Email not sent : " + e.toString());
                return Boolean.FALSE;
            }

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    @Transactional
    public Boolean deactivateRegisteredUser(String emailKey, String emailMessageKey) {
        User u = getUserByEmailKey(emailKey);

        if (u != null && u.getEmailVerificationKey() != null && u.getEmailVerificationKey().equals(emailKey) && u.getKeyValidity() != null) {
            if (u.getKeyValidity() != null && Duration.between(u.getKeyValidity(), Instant.now()).toMillis() > 0) {
                resetKeyEmail(u, Boolean.FALSE);
                uR.save(u);

                return Boolean.FALSE;
            }

            logger.info("unRegistering User with " + u.getEmailVerificationKey() + " Email Key.");
            logger.info("(" + u.getEmail() + ")");

            resetKeyEmail(u, Boolean.FALSE);
            String originalMail = u.getEmail();
            u.setEmail("disabled_" + originalMail);

            try {
                String title = null;
                Map<String, String> substitutions = new HashMap<>();
                substitutions.put("BASE_URL", emailManager.getBaseurl());
                substitutions.put("U_NAME", u.getName());
                substitutions.put("U_FIRSTNAME", u.getFirstname());

                switch (emailMessageKey) {
                    case RegisterEmailConstants.EMAIL_MESSAGE_PREFIX + "_INVALIDATION" ->
                        title = messageGetter.getEmailTitle(emailMessageKey + "_TITLE", null);
                    case ResetChallengeConstants.EMAIL_MESSAGE_PREFIX + "_INVALIDATION" ->
                        title = messageGetter.getEmailTitle(emailMessageKey + "_TITLE", null);
                    default ->
                        throw new AssertionError();
                }

                String message = messageGetter.getEmailMessage(emailMessageKey, substitutions);
                // send email to user
                String status = emailManager.sendSimpleMail(EmailDetails.builder()
                        .recipient(originalMail)
                        .subject(title)
                        .msgBody(message)
                        .build());
                logger.info("Email sent to user with " + status);

                // send Email to admin
                substitutions.put("ORIGINAL_MAIL", originalMail);
                String messageAdmin = messageGetter.getEmailMessage(emailMessageKey + EmailMessageConstants.EMAIL_ADMIN_SUFFIX,
                        substitutions
                );
                String statusAdmin = emailManager.sendSimpleMail(EmailDetails.builder()
                        .recipient(emailProperties.getEmail().getAdminEmail())
                        .subject(title)
                        .msgBody(messageAdmin)
                        .build());

                logger.info("Email sent to admin with " + statusAdmin);
            } catch (EmailSendingException e) {
                uR.save(u);
                logger.info("Email not sent : " + e.toString());
                return Boolean.FALSE;
            }

            uR.save(u);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    @Transactional
    public Boolean activateRegisteredUser(String emailKey) {
        User u = getUserByEmailKey(emailKey);

        if (u != null && u.getEmailVerificationKey() != null && u.getEmailVerificationKey().equals(emailKey) && u.getKeyValidity() != null) {
            if (Duration.between(u.getKeyValidity(), Instant.now()).toMillis() > 0) {
                resetKeyEmail(u, Boolean.FALSE);
                emailKeyForRegisterUser(u);

                uR.save(u);
                return Boolean.FALSE;
            }

            resetKeyEmail(u, Boolean.TRUE);

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    public User registerUser(User u, boolean toSave) {
        return registerUser(u, Boolean.FALSE, toSave);
    }

    @Override
    @Transactional
    public User registerUser(User u, Boolean isAdmin, boolean toSave) {
        System.out.println("org.loamoksecurity.manager.UserManager.registerUser()(étendu)");
        Role roleUser = rR.findByRole("ROLE_USER");

        if (isAdmin) {
            if (u.getRole() == null || u.getRole().getRole() == null || u.getRole().getRole().isBlank()) {
                throw new RuntimeException("user must have a Role but user.role is null.");
            }
            roleUser = rR.findByRole(u.getRole().getRole());
        }
        System.out.println("roleUser : " + roleUser);

        User user = User.builder()
                .password(u.getPassword())
                .passwordKey(u.getPasswordKey())
                .email(u.getEmail())
                .name(u.getName())
                .firstname(u.getFirstname())
                .role(roleUser)
                .gdproptin(u.isGdproptin())
                .enabled((isAdmin) ? true : false)
                .build();

        StringBuilder failedValidation = new StringBuilder();
        if (!doCheckUserRegistering(user, failedValidation)) {
            switch (failedValidation.toString()) {
                case "email" ->
                    throw new EmailAlreadyExistsException(user.getEmail());
                case "password" ->
                    throw new InvalidPasswordException();
                default ->
                    throw new MissingFieldsException(failedValidation.toString());
            }
        }

        logger.info("## UserManager : registerUser ##");
        
        String passwordChecksKey = checkPasswords(user.getPassword(), u.getPassword());
        logger.info("passwordChecksKey : " + passwordChecksKey);
        
        Boolean passwordCheck = (passwordChecksKey != null && passwordChecksKey.equals(user.getPasswordKey()));
        logger.info("passwordCheck : " + passwordCheck.toString());
        
        if(u.getPasswordKey() != null && passwordCheck.equals(Boolean.TRUE)) {
            user.setPassword("{bcrypt}" + passwordEncoder.encode(u.getPassword()));
        } else {
            throw new InvalidPasswordException();
        }

        emailKeyForRegisterUser(user);

        if (toSave) {
            try {
                uR.saveAndFlush(user);
                return user;
            } catch (RuntimeException e) {
                throw new RuntimeException("Error registering user : " + e.getMessage(), e);
            }
        }

        return user;
    }

    private User getUserByEmailKey(String emailKey) {
        User u = uR.findByEmailVerificationKey(emailKey);

        return u;
    }

    private void resetKeyEmail(User u, Boolean setEnabled) {
        u.setEnabled(setEnabled);
        u.setEmailVerificationKey(null);
        u.setKeyValidity(null);
        u.setAuthToken(null);
        u.setRememberMeToken(null);
    }

    private void emailKeyForRegisterUser(User u) {
        if (u.getEmailVerificationKey() == null) {
            Map<String, String> substitutions = new HashMap<>();
            substitutions.put("BASE_URL", emailManager.getBaseurl());
            substitutions.put("U_NAME", u.getName());
            substitutions.put("U_FIRSTNAME", u.getFirstname());

            String status = generateAndSetEmailVerificationKey(
                    u,
                    messageGetter.getEmailTitle(RegisterEmailConstants.EMAIL_MESSAGE_PREFIX + "_VALIDATION_TITLE", null),
                    messageGetter.getEmailMessage(RegisterEmailConstants.EMAIL_MESSAGE_PREFIX + "_VALIDATION", substitutions)
            );

            logger.info("Registering User with " + u.getEmailVerificationKey() + " Email Key.");
            logger.info("Email sent with " + status);
        }
    }

    private String generateAndSetEmailVerificationKey(User u, String title, String message) {
        String urlSafeSignature = generateKey(u);
        u.setEmailVerificationKey(urlSafeSignature);

        Instant actualDate = Instant.now();
        
        // Utilise la config au lieu de 1 heure en dur
        int keyValidityHours = securityProperties.getEmail().getKeyValidityHours();
        u.setKeyValidity(Date.from(actualDate.plus(Duration.ofHours(keyValidityHours))).toInstant());

        String separator = EmailMessageConstants.EMAIL_MESSAGE_SUBSTITUTION_SEPARATOR;
        message = message.replace(separator + "EMAIL_VERIFICATION_KEY" + separator, u.getEmailVerificationKey());

        String status = emailManager.sendSimpleMail(EmailDetails.builder()
                .recipient(u.getEmail())
                .subject(title)
                .msgBody(message)
                .build());

        return status;
    }

    private String generateKey(String source) {
        String hashedSignature = csb.buildHashedSignature(source);
        String urlSafeSignature = hashedSignature
                .replace('+', '-')
                .replace('/', '_')
                .replace("=", "");

        return urlSafeSignature;
    }
    
    private String generateKey(User u) {
        String randomToken = UUID.randomUUID().toString();

        String rawSignature = String.join("|",
                u.getEmail(), u.getName(),
                u.getFirstname(), u.getRole().getRole(),
                "" + System.currentTimeMillis(), randomToken
        );

        return generateKey(rawSignature);
    }

    @Override
    public Boolean doCheckUserRegistering(User u, StringBuilder failedValidation) {
        Boolean isEmailUnique = checkEmailUnique(u.getEmail());
        if (!isEmailUnique) {
            failedValidation.setLength(0);
            failedValidation.append("email");
            return Boolean.FALSE;
        }

        Boolean isPasswordCorrect = checkPasswordCorrect(u.getPassword());
        if (!isPasswordCorrect) {
            failedValidation.setLength(0);
            failedValidation.append("password");
            return Boolean.FALSE;
        }

        StringBuilder fieldName = new StringBuilder();
        Boolean areFieldsFilled = checkNonNullFields(u, fieldName);
        if (!areFieldsFilled) {
            failedValidation.setLength(0);
            failedValidation.append(fieldName);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public Boolean checkEmailUnique(String email) {
        if (email == null || email.isBlank()) {
            return Boolean.FALSE;
        }

        final User u = uR.findByEmail(email);

        Boolean isUnique = Boolean.FALSE;

        if (u == null) {
            isUnique = Boolean.TRUE;
        }

        return isUnique;
    }

    @Override
    public String checkPasswords(String password, String newPassword) {
        logger.info("### User Manager : checkPasswords ###");
        String res = null;
        if(checkPasswordCorrect(password, newPassword).equals(Boolean.TRUE)) {
            logger.info("passwords corrects !");
            String reversedPassword = new StringBuilder(newPassword).reverse().toString();
            res = generateKey("##" + password + "#"+ SECRET_KEY +"#" + reversedPassword + "##");
        }
        
        logger.info("res : " + res);
        return res;
    }

    @Override
    public Boolean checkNonNullFields(User u, StringBuilder fieldName) {
        if (u.getName() == null || u.getName().isBlank()) {
            fieldName.setLength(0);
            fieldName.append("name");
            return Boolean.FALSE;
        }

        if (u.getFirstname() == null || u.getFirstname().isBlank()) {
            fieldName.setLength(0);
            fieldName.append("firstname");
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean checkPasswordCorrect(String password, String newPassword) {
        if (password == null || password.isBlank() || newPassword == null || newPassword.isBlank()) {
            return Boolean.FALSE;
        }
        
        if(password.equals(newPassword)) {
            return checkPasswordCorrect(password);
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean checkPasswordCorrect(String password) {
        if (password == null || password.isBlank()) {
            return Boolean.FALSE;
        }

        return PATTERN.matcher(password).matches();
    }

}
