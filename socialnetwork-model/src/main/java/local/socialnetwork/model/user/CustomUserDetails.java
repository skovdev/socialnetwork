package local.socialnetwork.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CustomUserDetails {
    
    @Column(name = "country", nullable = false)
    private String country;
    
    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "birthday", nullable = false)
    private String birthday;

    @Column(name = "family_status", nullable = false)
    private String familyStatus;

    public CustomUserDetails() {

    }

    public String getCountry() {
        return country;
    }

    @JsonProperty(value = "country")
    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    @JsonProperty(value = "city")
    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    @JsonProperty(value = "address")
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    @JsonProperty(value = "phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    @JsonProperty(value = "birthday")
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFamilyStatus() {
        return familyStatus;
    }

    @JsonProperty(value = "familyStatus")
    public void setFamilyStatus(String familyStatus) {
        this.familyStatus = familyStatus;
    }
}