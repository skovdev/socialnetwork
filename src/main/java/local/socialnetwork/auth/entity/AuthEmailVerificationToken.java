package local.socialnetwork.auth.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import local.socialnetwork.shared.entity.AbstractBaseModel;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.time.Instant;

@Setter
@Getter
@Table(name = "auth_email_verification_tokens")
@Entity
public class AuthEmailVerificationToken extends AbstractBaseModel {

    @Column(name = "token", nullable = false, length = 32)
    private byte[] token;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUser authUser;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthEmailVerificationToken other)) return false;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hashCode(getId()) : System.identityHashCode(this);
    }

}
