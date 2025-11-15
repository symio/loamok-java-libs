package org.loamok.libs.o2.spring.security.repository;

import org.loamok.libs.o2.spring.security.entity.User;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Huby Franck
 */
public class SecuritySpecifications {
    private SecuritySpecifications() {
        super();
    }

    public static <T> Specification<T> belongsToUserOrAdmin(User user, boolean isAdmin) {
        return (root, query, cb) -> {
            if (isAdmin) {
                return cb.conjunction();
            }
            return cb.equal(root.get("user"), user);
        };
    }

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
