package local.socialnetwork.auth.service;

import local.socialnetwork.auth.entity.AuthEmailVerificationToken;

public interface TokenService {
    String generateAuthToken();
    byte[] hashToken(String token);
    void save(AuthEmailVerificationToken authEmailVerificationToken);
}
