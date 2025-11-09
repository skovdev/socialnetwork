package local.socialnetwork.auth.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.CascadeType;

import local.socialnetwork.shared.entity.AbstractBaseModel;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Set;
import java.util.HashSet;

import java.time.Instant;


@Setter
@Getter
@Entity
@Table(name = "auth_users")
public class AuthUser extends AbstractBaseModel {

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AuthStatus authStatus;

    @OneToMany(mappedBy = "authUser", cascade = CascadeType.ALL)
    private Set<AuthUserRole> authUserRoles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false,  updatable = false)
    private Instant createdAt;

}
