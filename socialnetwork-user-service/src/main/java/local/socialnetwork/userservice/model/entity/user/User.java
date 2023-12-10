package local.socialnetwork.userservice.model.entity.user;

import local.socialnetwork.userservice.model.entity.AbstractBaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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
    String familyStatus;

    @Type(type = "pg-uuid")
    @Column(name = "auth_user_id", nullable = false, unique = true)
    UUID authUserId;

}