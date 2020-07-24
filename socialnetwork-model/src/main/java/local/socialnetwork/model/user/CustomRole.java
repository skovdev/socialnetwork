package local.socialnetwork.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import local.socialnetwork.model.AbstractBaseModel;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "sn_roles")
public class CustomRole extends AbstractBaseModel {

    @Column(name = "authority", nullable = false)
    private String authority;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", nullable = false)
    private CustomUser customUser;

    public CustomRole() {

    }

	public String getAuthority() {
		return authority;
	}

	@JsonProperty(value = "authority")
	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@JsonManagedReference
	public CustomUser getCustomUser() {
		return customUser;
	}

	public void setCustomUser(CustomUser customUser) {
		this.customUser = customUser;
	}
}