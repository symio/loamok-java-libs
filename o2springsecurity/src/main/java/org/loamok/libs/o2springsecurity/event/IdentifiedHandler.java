package org.loamok.libs.o2springsecurity.event;

import java.util.Collection;
import org.loamok.libs.o2springsecurity.entity.User;
import org.loamok.libs.o2springsecurity.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Meta Handler pour travailler sur les ressources d'un utilisateur identifié
 *
 * @author Huby Franck
 */
public abstract class IdentifiedHandler {
    /**
     * Repository utilisateur
     */
    protected final UserRepository userRepository;

    /**
     * Constructeur par défaut pour injection de dépendances
     * @param userRepository Repository utilisateur
     */
    protected IdentifiedHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Auth helper
     * 
     * @return Authentication
     */
    protected Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Getter clientId
     * 
     * @return le nom d'utilisateur
     */
    protected String getClientId() {
        return (String) getAuth().getPrincipal();
    }

    /**
     * Authorités
     * 
     * @return Collection
     */
    protected Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuth().getAuthorities();
    }

    /**
     * Dispose ou non de ce scope
     * 
     * @param scope le nom du scope
     * @return vrai ou faux
     */
    protected boolean hasScope(String scope) {
        return getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SCOPE_" + scope));
    }

    /**
     * Utilisateur connecté à l'application
     * 
     * @return Utilisateur
     */
    protected User getCurrentUser() {
        return userRepository.findByEmail(getClientId());
    }

    /**
     * Utilisateur connecté est admin et dispose du scope admin
     * 
     * @return vrai ou faux
     */
    protected boolean isAdminWithScopeAdmin() {
        User user = getCurrentUser();
        return Boolean.TRUE.equals(user.getRole().getIsAdmin()) && hasScope("admin");
    }
}
