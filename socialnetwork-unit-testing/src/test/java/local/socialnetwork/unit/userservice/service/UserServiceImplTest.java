package local.socialnetwork.unit.userservice.service;

//import local.socialnetwork.userservice.model.entity.user.CustomRole;
//import local.socialnetwork.userservice.model.entity.user.CustomUser;
//import local.socialnetwork.userservice.model.entity.user.CustomUserDetails;

//import local.socialnetwork.userservice.repository.UserRepository;

//import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
//import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - unit tests")
public class UserServiceImplTest {

//    @Mock
//    private UserRepository userRepository;

    @Test
    public void shouldUpdateUser() {

//        CustomUser customUser = new CustomUser();
//
//        customUser.setId(UUID.randomUUID());
//        customUser.setFirstName("firstName");
//        customUser.setLastName("lastName");
//        customUser.setUsername("username");
//        customUser.setPassword("password");
//
//        CustomUserDetails customUserDetails = new CustomUserDetails();
//
//        customUserDetails.setCountry("country");
//        customUserDetails.setCity("city");
//        customUserDetails.setAddress("address");
//        customUserDetails.setPhone("phone");
//        customUserDetails.setBirthday("birthday");
//        customUserDetails.setFamilyStatus("familyStatus");
//
//        customUser.setUserDetails(customUserDetails);
//
//        CustomRole customRole = new CustomRole();
//
//        customRole.setId(UUID.randomUUID());
//        customRole.setAuthority("authority");
//        customRole.setUser(customUser);
//
//        customUser.setRoles(List.of(customRole));
//
//        Mockito.when(userRepository.findById(UUID.randomUUID()))
//                .thenReturn(Optional.of(customUser));
//
//        CustomUser updatedUser = userRepository.save(customUser);
//
//        Assertions.assertEquals(customUser.getId(), updatedUser.getId());
//        Assertions.assertEquals(customUser.getFirstName(), updatedUser.getFirstName());
//        Assertions.assertEquals(customUser.getLastName(), updatedUser.getLastName());
//        Assertions.assertEquals(customUser.getUsername(), updatedUser.getUsername());
//        Assertions.assertEquals(customUser.getPassword(), updatedUser.getPassword());
//        Assertions.assertEquals(customUser.getUserDetails().getCountry(), updatedUser.getUserDetails().getCountry());
//        Assertions.assertEquals(customUser.getUserDetails().getCity(), updatedUser.getUserDetails().getCity());
//        Assertions.assertEquals(customUser.getUserDetails().getAddress(), updatedUser.getUserDetails().getAddress());
//        Assertions.assertEquals(customUser.getUserDetails().getPhone(), updatedUser.getUserDetails().getPhone());
//        Assertions.assertEquals(customUser.getUserDetails().getBirthday(), updatedUser.getUserDetails().getBirthday());
//        Assertions.assertEquals(customUser.getUserDetails().getFamilyStatus(), updatedUser.getUserDetails().getFamilyStatus());
//        Assertions.assertEquals(customUser.getRoles().get(0).getId(), updatedUser.getRoles().get(0).getId());
//        Assertions.assertEquals(customUser.getRoles().get(0).getAuthority(), updatedUser.getRoles().get(0).getAuthority());

    }
}