package local.socialnetwork.likes.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;

import local.socialnetwork.auth.entity.AuthUser;

import local.socialnetwork.posts.entity.Post;

import local.socialnetwork.shared.entity.AbstractBaseModel;

import lombok.Setter;
import lombok.Getter;

import java.time.Instant;

import java.util.Objects;

/**
 * A single like placed by an {@link AuthUser} on a {@link Post}. A user may like a given post at
 * most once; this is enforced by a unique constraint on {@code (post_id, author_id)}.
 */
@Setter
@Getter
@Table(name = "likes", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "author_id"}))
@Entity
public class Like extends AbstractBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private AuthUser author;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Like other)) return false;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hashCode(getId()) : System.identityHashCode(this);
    }

}
