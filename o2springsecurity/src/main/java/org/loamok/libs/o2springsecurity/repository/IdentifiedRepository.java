package org.loamok.libs.o2springsecurity.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;
import java.util.function.LongSupplier;
import org.loamok.libs.o2springsecurity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Meta repository pour retrouver les ressources d'un utilisateur identifié
 *
 * @author Huby Franck
 */
public abstract class IdentifiedRepository {
    /**
     * Entity manager
     */
    @PersistenceContext
    protected EntityManager em;
    /**
     * Repository utilisateur
     */
    protected final UserRepository userRepository;

    /**
     * Constructeur
     * 
     * @param userRepository Repository utilisateur
     */
    protected IdentifiedRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Auth helpers
     * 
     * @return Authentication
     */
    protected Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Getter clientId
     * @return le nom d'utilisateur
     */
    protected String getClientId() {
        return (String) getAuth().getPrincipal();
    }

    /**
     * Authorités
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
     * Utilisateur en connecté à l'application
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

    /**
     * Utilitaire pour la pagination des résultats
     * 
     * @param <T> Objet
     * @param query Requête
     * @param pageable Paginateur
     * @param countSupplier Fournisseur de "select count"
     * @return une requête paginée
     */
    protected <T> Page<T> paginateQuery(TypedQuery<T> query,
                                        Pageable pageable,
                                        LongSupplier countSupplier) {
        List<T> content = query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = countSupplier.getAsLong();
        return new PageImpl<>(content, pageable, total);
    }
}
