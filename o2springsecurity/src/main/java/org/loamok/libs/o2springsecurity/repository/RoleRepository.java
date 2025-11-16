package org.loamok.libs.o2springsecurity.repository;

import org.loamok.libs.o2springsecurity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Repository Spring Data Rest pour Role
 *
 * @author Huby Franck
 */
@RepositoryRestResource(collectionResourceRel = "roles", path = "roles")
public interface RoleRepository extends JpaRepository<Role, Integer> {
    /**
     * Retourne un rôle par son nom
     * Ressource Rest
     * 
     * @param role Nom du rôle
     * @return un rôle
     */
    @Query
    @RestResource(path = "findByRole", rel = "findByRole")
    Role findByRole(@Param("role") String role);
}
