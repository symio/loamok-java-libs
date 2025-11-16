package org.loamok.libs.o2springsecurity.repository;

import java.util.List;
import org.loamok.libs.o2springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Repository Spring Data Rest pour Utilisateur
 *
 * @author Huby Franck
 */
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Retrouve un Utilisateur par son adresse e-mail
     * 
     * @param email Adresse e-mail
     * @return Utilisateur
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);
    /**
     * Retrouve un Utilisateur par son adresse e-mail et son mot de passe
     * 
     * @param email Adresse e-mail
     * @param password Mot de passe
     * @return Utilisateur
     */
    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
    /**
     * Retrouve un Utilisateur par sa clé d'activation e-mail
     * 
     * @param emailVerificationKey Clé d'activation e-mail
     * @return Utilisateur
     */
    User findByEmailVerificationKey(@Param("emailVerificationKey") String emailVerificationKey);
    /**
     * Retourne une liste d'utilisateurs dont l'e-mail contiens la chaîne en paramètre
     * Ressource Rest
     * 
     * @param email E-mail ou partie d'e-mail
     * @return Liste d'utilisateurs
     */
    @RestResource(path = "findByEmailContaining", rel = "findByEmailContaining")
    List<User> findByEmailContaining(@Param("email") String email);
    /**
     * Retourne une liste d'utilisateurs dont le nom contiens la chaîne en paramètre
     * Ressource Rest
     * 
     * @param name Nom ou partie du nom
     * @return Liste d'utilisateurs
     */
    @RestResource(path = "findByNameContaining", rel = "findByNameContaining") 
    List<User> findByNameContaining(@Param("name") String name);
    /**
     * Retourne une liste d'utilisateurs disposant du rôle passé en paramètre
     * Ressource Rest
     * 
     * @param role Nom du rôle
     * @return Liste d'utilisateurs
     */
    @RestResource(path = "findByRole", rel = "findByRole")
    List<User> findByRole_Role(@Param("role") String role);
}
