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

@Setter
@Getter
@Table(name = "auth_user_roles")
@Entity
public class AuthUserRole extends AbstractBaseModel {

    @Column(name = "authority")
    private String authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUser authUser;

}
