package local.socialnetwork.userservice.model.entity;

import local.socialnetwork.userservice.model.AbstractBaseModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "sn_user")
@Entity
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AbstractBaseModel {

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "country")
    String country;

    @Column(name = "city")
    String city;

    @Column(name = "address")
    String address;

    @Column(name = "phone")
    String phone;

    @Column(name = "birth_day")
    String birthDay;

    @Column(name = "family_status")
    String familyStatus;

    @Column(name = "auth_user_id", nullable = false)
    String authUserId;

}