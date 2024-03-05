package local.socialnetwork.userservice.event;

import local.socialnetwork.userservice.entity.user.User;

public record ProfileCompletedEvent(User user) {}