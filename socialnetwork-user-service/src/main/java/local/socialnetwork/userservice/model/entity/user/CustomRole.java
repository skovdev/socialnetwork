package local.socialnetwork.userservice.model.entity.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.userservice.model.AbstractBaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sn_roles")
public class CustomRole extends AbstractBaseModel {

    @Column(name = "authority", nullable = false)
    private String authority;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private CustomUser user;


    public String getAuthority() {
        return authority;
    }

    @JsonProperty(value = "authority")
    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @JsonBackReference
    public CustomUser getUser() {
        return user;
    }

    @JsonProperty(value = "user")
    public void setUser(CustomUser user) {
        this.user = user;
    }
}