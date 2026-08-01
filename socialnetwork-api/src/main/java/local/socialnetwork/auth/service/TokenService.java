package local.socialnetwork.auth.service;

import local.socialnetwork.auth.entity.AuthEmailVerificationToken;

/**
 * Service for generating and hashing single-use verification tokens.
 */
public interface TokenService {
    String generateAuthToken();
    byte[] hashToken(String token);
    void save(AuthEmailVerificationToken authEmailVerificationToken);
}
