package local.socialnetwork.core.service.implementation;

import local.socialnetwork.common.auth.AuthenticationHelper;

import local.socialnetwork.core.TestUtil;

import local.socialnetwork.core.repository.UserRepository;
import local.socialnetwork.core.repository.ProfileRepository;

import local.socialnetwork.core.service.impl.ProfileServiceImpl;

import local.socialnetwork.core.util.ResourceUtil;

import local.socialnetwork.model.user.CustomUser;
import local.socialnetwork.model.user.CustomUserDetails;

import local.socialnetwork.model.profile.Profile;

import org.junit.Assert;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import static local.socialnetwork.core.TestUtil.randomBoolean;
import static local.socialnetwork.core.TestUtil.randomString;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceImplTest {

    private final static String pathDefaultAvatar = randomString();
    private final static String pathUploadAvatar = randomString();

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @Mock
    private ResourceUtil resourceUtil;

    @InjectMocks
    private ProfileServiceImpl profileServiceImpl;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(profileServiceImpl, "pathDefaultAvatar", pathDefaultAvatar);
        ReflectionTestUtils.setField(profileServiceImpl, "pathUploadAvatar", pathUploadAvatar);
    }

    @Test
    public void findAllTest() {

        Profile firstProfile = new Profile();

        firstProfile.setId(TestUtil.randomUUID());

        Profile secondProfile = new Profile();

        secondProfile.setId(TestUtil.randomUUID());

        Profile thirdProfile = new Profile();

        thirdProfile.setId(TestUtil.randomUUID());

        List<Profile> profiles = new ArrayList<>();

        profiles.add(firstProfile);
        profiles.add(secondProfile);
        profiles.add(thirdProfile);

        Mockito.when(profileRepository.findAll()).thenReturn(profiles);

        List<Profile> returnedProfiles = profileServiceImpl.findAll();

        Assert.assertEquals(profiles, returnedProfiles);

        Mockito.verify(profileRepository).findAll();

    }

    @Test
    public void findByIdTest() {

        UUID uuid = TestUtil.randomUUID();

        Profile profile = new Profile();

        profile.setId(TestUtil.randomUUID());

        Mockito.when(profileRepository.getOne(uuid)).thenReturn(profile);

        Profile returnedProfile = profileServiceImpl.findById(uuid);

        Assert.assertEquals(profile, returnedProfile);

        Mockito.verify(profileRepository).getOne(uuid);

    }

    @Test
    public void findProfileByUsernameTest() {

        String username = randomString();

        Profile profile = new Profile();

        profile.setId(TestUtil.randomUUID());

        Mockito.when(profileRepository.findProfileByUsername(username)).thenReturn(profile);

        Profile returnedProfile = profileServiceImpl.findProfileByUsername(username);

        Assert.assertEquals(profile, returnedProfile);

        Mockito.verify(profileRepository).findProfileByUsername(username);

    }

    @Test
    public void findProfileByFirstNameTest() {

        String firstName = randomString();

        Profile firstProfile = new Profile();

        firstProfile.setId(TestUtil.randomUUID());

        Profile secondProfile = new Profile();

        secondProfile.setId(TestUtil.randomUUID());

        Profile thirdProfile = new Profile();

        thirdProfile.setId(TestUtil.randomUUID());

        List<Profile> profiles = new ArrayList<>();

        profiles.add(firstProfile);
        profiles.add(secondProfile);
        profiles.add(thirdProfile);

        Mockito.when(profileRepository.findProfilesByFirstName(firstName)).thenReturn(profiles);

        List<Profile> returnedProfiles = profileServiceImpl.findProfilesByFirstName(firstName);

        Assert.assertEquals(profiles, returnedProfiles);

        Mockito.verify(profileRepository).findProfilesByFirstName(firstName);

    }

    @Test
    public void saveTest() {

        Profile profile = new Profile();

        profile.setId(TestUtil.randomUUID());

        Mockito.when(profileRepository.save(profile)).thenReturn(profile);

        profileServiceImpl.save(profile);

        Mockito.verify(profileRepository).save(profile);

    }

    @Test
    public void deleteTest() {

        UUID id = TestUtil.randomUUID();

        Mockito.doNothing().when(profileRepository).deleteById(id);

        profileServiceImpl.delete(id);

        Mockito.verify(profileRepository).deleteById(id);

    }

    @Test
    public void deleteAvatarProfileByUsernameTest() throws IOException {

        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        String username = randomString();
        String country = randomString();
        String city = randomString();
        String address = randomString();
        boolean isActive = randomBoolean();
        String avatar = randomString();

        CustomUser customUser = new CustomUser();

        customUser.setId(userId);
        customUser.setUsername(username);

        CustomUserDetails customUserDetails = new CustomUserDetails();

        customUserDetails.setCountry(country);
        customUserDetails.setCity(city);
        customUserDetails.setAddress(address);

        customUser.setCustomUserDetails(customUserDetails);

        Profile profile = new Profile();

        profile.setId(profileId);
        profile.setActive(isActive);
        profile.setAvatar(avatar);
        profile.setCustomUser(customUser);

        customUser.setProfile(profile);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(customUser);
        Mockito.when(resourceUtil.getEncodedResource(pathDefaultAvatar)).thenReturn(pathDefaultAvatar);

        profileServiceImpl.setDefaultAvatar(username);

        Mockito.verify(userRepository).findByUsername(username);
        Mockito.verify(resourceUtil).getEncodedResource(pathDefaultAvatar);

    }

    @Test
    public void addAvatarProfileTest() throws IOException {

        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        String username = randomString();
        String country = randomString();
        String city = randomString();
        String address = randomString();
        boolean isActive = randomBoolean();
        String avatar = randomString();

        CustomUser customUser = new CustomUser();

        customUser.setId(userId);
        customUser.setUsername(username);

        CustomUserDetails customUserDetails = new CustomUserDetails();

        customUserDetails.setCountry(country);
        customUserDetails.setCity(city);
        customUserDetails.setAddress(address);

        customUser.setCustomUserDetails(customUserDetails);

        Profile profile = new Profile();

        profile.setId(profileId);
        profile.setActive(isActive);
        profile.setAvatar(avatar);
        profile.setCustomUser(customUser);

        customUser.setProfile(profile);

        MockMultipartFile file = new MockMultipartFile(avatar, avatar.getBytes());

        Mockito.when(authenticationHelper.getAuthenticatedUser()).thenReturn(customUser);
        Mockito.when(resourceUtil.writeResource(file, pathUploadAvatar)).thenReturn(avatar);

        profileServiceImpl.updateAvatarProfile(file);

        Mockito.verify(resourceUtil).writeResource(file, pathUploadAvatar);
        Mockito.verify(profileRepository).save(profile);

    }

    @Test
    public void changeStatusTest() {

        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        String username = randomString();
        String country = randomString();
        String city = randomString();
        String address = randomString();
        boolean isActive = randomBoolean();
        String avatar = randomString();

        CustomUser customUser = new CustomUser();

        customUser.setId(userId);
        customUser.setUsername(username);

        CustomUserDetails customUserDetails = new CustomUserDetails();

        customUserDetails.setCountry(country);
        customUserDetails.setCity(city);
        customUserDetails.setAddress(address);

        customUser.setCustomUserDetails(customUserDetails);

        Profile profile = new Profile();

        profile.setId(profileId);
        profile.setActive(isActive);
        profile.setAvatar(avatar);
        profile.setCustomUser(customUser);

        customUser.setProfile(profile);

        Mockito.when(authenticationHelper.getAuthenticatedUser()).thenReturn(customUser);

        profileServiceImpl.changeStatus(username, true);

        Mockito.verify(profileRepository).save(profile);

    }
}