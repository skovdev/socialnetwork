package local.socialnetwork.userservice.entity.user;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import local.socialnetwork.userservice.entity.AbstractBaseModel;

import local.socialnetwork.userservice.type.FamilyStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.util.UUID;

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
    @Enumerated(EnumType.STRING)
    FamilyStatus familyStatus;

    @Column(name = "auth_user_id", nullable = false, unique = true)
    UUID authUserId;

}