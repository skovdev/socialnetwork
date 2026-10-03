package local.socialnetwork.follows.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;

import local.socialnetwork.auth.entity.AuthUser;

import local.socialnetwork.shared.entity.AbstractBaseModel;

import lombok.Setter;
import lombok.Getter;

import java.time.Instant;

import java.util.Objects;

/**
 * A single follow relationship: {@link #follower} follows {@link #followed}. A user may follow a
 * given other user at most once; this is enforced by a unique constraint on
 * {@code (follower_id, followed_id)}.
 */
@Setter
@Getter
@Table(name = "follows", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}))
@Entity
public class Follow extends AbstractBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private AuthUser follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private AuthUser followed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Follow other)) return false;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hashCode(getId()) : System.identityHashCode(this);
    }

}
