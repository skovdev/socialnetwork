package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import local.socialnetwork.profileservice.query.FindAllProfilesQuery;

import local.socialnetwork.profileservice.query.FindProfileByUserIdQuery;
import local.socialnetwork.profileservice.service.ProfileQueryService;

import org.axonframework.messaging.responsetypes.ResponseTypes;

import org.axonframework.queryhandling.QueryGateway;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import java.util.stream.Collectors;

@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private QueryGateway queryGateway;

    @Autowired
    public void setQueryGateway(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public List<ProfileDto> findAll() throws ExecutionException, InterruptedException {

        List<Profile> profile = queryGateway.query(new FindAllProfilesQuery(), ResponseTypes.multipleInstancesOf(Profile.class)).get();

        return profile.stream().map(p -> {

            ProfileDto profileDto = new ProfileDto();

            profileDto.setId(p.getId());
            profileDto.setAvatar(p.getAvatar());
            profileDto.setAvatar(p.getAvatar());
            profileDto.setUserId(p.getUserId());

            return profileDto;
        }).collect(Collectors.toList());
    }

    @Override
    public ProfileDto findByUserId(UUID id) throws ExecutionException, InterruptedException {

        Profile profile = queryGateway.query(new FindProfileByUserIdQuery(id), ResponseTypes.instanceOf(Profile.class)).get();

        if (profile != null) {

            ProfileDto profileDto = new ProfileDto();

            profileDto.setId(profile.getId());
            profileDto.setActive(profile.isActive());
            profileDto.setAvatar(profile.getAvatar());
            profileDto.setUserId(profile.getUserId());

            return profileDto;

        }

        throw new NullPointerException();

    }
}
