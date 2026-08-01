package local.socialnetwork.comments.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import local.socialnetwork.auth.entity.AuthUser;

import local.socialnetwork.posts.entity.Post;

import local.socialnetwork.shared.entity.AbstractBaseModel;

import lombok.Setter;
import lombok.Getter;

import java.time.Instant;

import java.util.Objects;

/**
 * A comment on a {@link Post}, optionally nested as a reply to another comment.
 */
@Setter
@Getter
@Table(name = "comments")
@Entity
public class Comment extends AbstractBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private AuthUser author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment other)) return false;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? Objects.hashCode(getId()) : System.identityHashCode(this);
    }

}
