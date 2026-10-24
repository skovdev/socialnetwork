package local.socialnetwork.auth.event;

public record VerificationEmailRequested(String email, String rawToken) {}
