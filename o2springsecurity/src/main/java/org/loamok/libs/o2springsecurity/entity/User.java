package org.loamok.libs.o2springsecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.loamok.libs.o2springsecurity.jwt.JwtService;
import org.loamok.libs.o2springsecurity.jwt.JwtServiceImpl;
import org.loamok.libs.o2springsecurity.util.SpringContextUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Entité Utilisateur
 *
 * @author Huby Franck
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "name", "firstname", "email", "enabled", "gdproptin"})
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
@Entity
@Table(name = "users", indexes = {
    @Index(columnList = "email"),
    @Index(columnList = "auth_token"),
    @Index(columnList = "email_verification_key"),
    @Index(columnList = "remember_me_token")
})
public class User implements UserDetails {
    /**
     * Identifiant de l'utilisateur
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id;
    /**
     * Nom de l'utilisateur
     */
    @Column(name = "name", nullable = false, length = 40)
    private String name;
    /**
     * Prénom de l'utilisateur
     */
    @Column(name = "firstname", nullable = false, length = 50)
    private String firstname;
    /**
     * Adresse e-mail de l'utilisateur (et username)
     */
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;
    /**
     * Statut d'acceptation des conditions d'utilisation et politique de confidentialisé
     */
    @Column(name = "gdproptin", nullable = true)
    private boolean gdproptin;
    /**
     * Datetime de création de l'utilisateur
     */
    @CreationTimestamp
    private Instant createdAt;
    /**
     * Datetime de dernière modification de l'utilisateur
     */
    @UpdateTimestamp
    private Instant updatedAt;
    // -- relations
    /**
     * Rôle de l'utilisateur
     */
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Role role;
    // sécurité (Mot de passe, tokens, ...)
    /**
     * Mot de passe de l'utilisateur
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false, length = 69)
    private String password;
    /**
     * Token Oauth2 (token "serveur")
     */
    @JsonIgnore
    @Column(name = "auth_token", nullable = true, columnDefinition = "TEXT")
    private String authToken;
    /**
     * Token d'autoconnection (token "Serveur")
     */
    @JsonIgnore
    @Column(name = "remember_me_token", nullable = true, columnDefinition = "TEXT")
    private String rememberMeToken;
    /**
     * Clé unique de validation e-mail (sert aux challenges de sécurité)
     */
    @JsonIgnore
    @Column(name = "email_verification_key", nullable = true, columnDefinition = "TEXT")
    private String emailVerificationKey;
    /**
     * Durée de validité de cette clé e-mail
     */
    @JsonIgnore
    @Column(name = "key_validity", nullable = true)
    private Instant keyValidity;
    /**
     * Propriété virtuelle pour l'authentification Oauth JWT
     */
    @JsonIgnore
    @Transient
    private String authority;
    /**
     * Droits admin de l'utilisateur
     */
    @JsonIgnore
    @Transient
    private Boolean isAdmin;
    /**
     * Utilisateur activé ? 
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean enabled;

    /**
     * Liste des accès autorisés
     * 
     * @return Collection
     */
    @Override
    @Transient
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        authority = role.getRole();
        isAdmin = role.getIsAdmin();
        return Arrays.asList(new SimpleGrantedAuthority(authority));
    }

    /**
     * Getter sur authority
     * 
     * @return Nom du rôle
     */
    public String getAuthority() {
        return role.getRole();
    }
    
    /**
     * Getter Username
     * 
     * @return l'adresse e-mail de l'utilisateur
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Est-ce que le compte n'est pas expiré ? 
     * 
     * @return vrai ou faux
     */
    @Override
    public boolean isAccountNonExpired() {
        return (enabled != null && enabled) && (gdproptin);
    }

    /**
     * Est-ce que le compte est non vérouillé ? 
     * 
     * @return vrai ou faux
     */
    @Override
    public boolean isAccountNonLocked() {
        return (enabled != null && enabled) && (gdproptin);
    }

    /**
     * Est-ce que l'accès est actif ? 
     * 
     * @return vrai ou faux
     */
    @Override
    public boolean isCredentialsNonExpired() {
        if (authToken != null && !authToken.isEmpty()) {
            try {
                JwtService jwtService = SpringContextUtil.getBean(JwtServiceImpl.class);
                boolean nonExpired = jwtService.isClientCredentialsTokenValid(this.authToken, this.email);
        
                return nonExpired;
            } catch (Exception e) {
                return false;
            }
        }
        
        return false;
    }

    /**
     * Utilisateur activé ? 
     * 
     * @return vrai ou faux
     */
    @Override
    public boolean isEnabled() {
        return (enabled != null && enabled) && (gdproptin);
    }
    
    /**
     * Getter virtuel pour l'id de Rôle
     * 
     * @return un entier
     */
    @JsonProperty("roleId")
    public Integer getRoleId() {
        return role != null ? role.getId() : null;
    }
}
