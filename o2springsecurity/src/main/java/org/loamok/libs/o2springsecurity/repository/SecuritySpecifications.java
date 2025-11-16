package org.loamok.libs.o2springsecurity.repository;

import org.loamok.libs.o2springsecurity.entity.User;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filtrer un contenu en base comme appartenant à l'utilisateur
 * Permet l'usage de Prédicats
 *
 * @author Huby Franck
 */
public class SecuritySpecifications {
    private SecuritySpecifications() {
        super();
    }

    /**
     * Est relatif à un utilisateur ou l'utilisateur est admin
     * 
     * @param <T> Objet dont on veut vérifer l'appartenance
     * @param user Utilisateur en cours
     * @param isAdmin Est admin ? 
     * @return Specification
     */
    public static <T> Specification<T> belongsToUserOrAdmin(User user, boolean isAdmin) {
        return (root, query, cb) -> {
            if (isAdmin) {
                return cb.conjunction();
            }
            return cb.equal(root.get("user"), user);
        };
    }

    /**
     * Par ID et
     * Est relatif à un utilisateur ou l'utilisateur est admin
     * 
     * @param <T> Objet dont on veut vérifer l'appartenance
     * @param id id de l'objet
     * @param user Utilisateur en cours
     * @param isAdmin Est admin ? 
     * @return Specification
     */
    public static <T> Specification<T> byIdAndBelongsToUserOrAdmin(Integer id, User user, boolean isAdmin) {
        return (root, query, cb) -> {
            if (isAdmin) {
                return cb.equal(root.get("id"), id);
            }
            return cb.and(
                cb.equal(root.get("id"), id),
                cb.equal(root.get("user"), user)
            );
        };
    }
}
