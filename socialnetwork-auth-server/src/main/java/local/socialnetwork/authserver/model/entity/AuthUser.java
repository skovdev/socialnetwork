package local.socialnetwork.authserver.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

import java.util.List;

@Table(name = "sn_auth_user")
@Entity
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthUser extends AbstractBaseModel {

    @Column(name = "username", nullable = false)
    String username;

    @Column(name = "password", nullable = false)
    String password;

    @OneToMany(mappedBy = "authUser", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<AuthRole> authRoles;

}
