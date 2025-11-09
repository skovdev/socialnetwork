package local.socialnetwork.auth.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import local.socialnetwork.shared.entity.AbstractBaseModel;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import java.time.Instant;

@Setter
@Getter
@Table(name = "auth_refresh_tokens")
@Entity
public class AuthRefreshToken extends AbstractBaseModel {

    @Column(columnDefinition = "uuid")
    private UUID jti;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUser user;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

}
