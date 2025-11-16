package local.socialnetwork.profiles.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;

import local.socialnetwork.auth.entity.AuthUser;

import local.socialnetwork.shared.entity.AbstractBaseModel;

import lombok.Setter;
import lombok.Getter;

import java.time.LocalDate;

@Setter
@Getter
@Table(name = "user_profiles")
@Entity
public class UserProfile extends AbstractBaseModel {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AuthUser authUser;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "bio")
    private String biography;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone")
    private String phoneNumber;

    @Column(name = "birth_date")
    private String country;

    @Column(name = "birth_date")
    private String city;

    @Column(name = "birth_date")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "family_status")
    private FamilyStatus familyStatus;

}
