package local.socialnetwork.userservice.mapping;

import local.socialnetwork.userservice.model.dto.user.RoleDto;
import local.socialnetwork.userservice.model.dto.user.UserDetailsDto;
import local.socialnetwork.userservice.model.dto.user.UserDto;

import local.socialnetwork.userservice.model.entity.user.CustomUser;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MappingObject {

    public UserDto convertUserToUserDto(CustomUser user) {

        UserDto userDto = new UserDto();

        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());

        UserDetailsDto userDetailsDto = new UserDetailsDto();

        userDetailsDto.setCountry(user.getUserDetails().getCountry());
        userDetailsDto.setCity(user.getUserDetails().getCity());
        userDetailsDto.setAddress(user.getUserDetails().getAddress());
        userDetailsDto.setPhone(user.getUserDetails().getPhone());
        userDetailsDto.setBirthday(user.getUserDetails().getBirthday());
        userDetailsDto.setFamilyStatus(user.getUserDetails().getFamilyStatus());

        List<RoleDto> roles = new ArrayList<>();

        user.getRoles().forEach(role -> {

            RoleDto roleDto = new RoleDto();

            roleDto.setId(role.getId());
            roleDto.setAuthority(role.getAuthority());
            roleDto.setUser(userDto);

            roles.add(roleDto);

        });

        userDto.setRoles(roles);

        return userDto;

    }
}
