package local.socialnetwork.auth.service.impl;

import local.socialnetwork.auth.entity.AuthEmailVerificationToken;

import local.socialnetwork.auth.repository.AuthEmailVerificationTokenRepository;

import local.socialnetwork.auth.service.TokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SHA_256_ALGORITHM = "SHA-256";

    private final AuthEmailVerificationTokenRepository authEmailVerificationTokenRepository;

    @Override
    public String generateAuthToken() {
        var bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public byte[] hashToken(String token) {
        try {
            var messageDigest = MessageDigest.getInstance(SHA_256_ALGORITHM);
            return messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    @Override
    public void save(AuthEmailVerificationToken authEmailVerificationToken) {
        authEmailVerificationTokenRepository.save(authEmailVerificationToken);
    }
}
