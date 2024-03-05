package local.socialnetwork.authserver.event;

import local.socialnetwork.authserver.dto.SignUpDto;

import java.util.UUID;

public record UserDetailsEvent(SignUpDto signUp, UUID authUserId) {}
