package local.socialnetwork.authserver.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

@Table(name = "sn_auth_role")
@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRole extends AbstractBaseModel {

    @Column(name = "authority", nullable = false)
    String authority;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    AuthUser authUser;

}