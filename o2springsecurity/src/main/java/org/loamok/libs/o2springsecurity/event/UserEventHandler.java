package org.loamok.libs.o2springsecurity.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loamok.libs.o2springsecurity.entity.User;
import org.loamok.libs.o2springsecurity.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

/**
 * Gère les évènements du cycle de vie User
 *
 * @author Huby Franck
 */
@Component
@RepositoryEventHandler(User.class)
public class UserEventHandler {

    /**
     * Journalisation
     */
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private UserManager userManager;

    /**
     * Gestion de la création d'un utilisateur
     * 
     * @param user Utilisateur à enregistrer
     */
    @HandleBeforeCreate
    public void handleUtilisateurCreate(User user) {
        logger.info("### UserEventHandler - user reçu : " + user);
        logger.info("### UserEventHandler - passwordKey : " + user.getPasswordKey());
    
        
        User cleanUser = userManager.registerUser(user, false);

        user.setName(cleanUser.getName());
        user.setFirstname(cleanUser.getFirstname());
        user.setEmail(cleanUser.getEmail());
        user.setPassword(cleanUser.getPassword());
        user.setRole(cleanUser.getRole());
        user.setEnabled(cleanUser.getEnabled());
        user.setGdproptin(cleanUser.isGdproptin());
        
        user.setAuthToken(null);
        user.setRememberMeToken(null);
        user.setEmailVerificationKey(cleanUser.getEmailVerificationKey());
        user.setKeyValidity(cleanUser.getKeyValidity());
    }
}
