package local.socialnetwork.auth.service.impl;

/**
 * Canonical forms for identifiers that are compared case-insensitively.
 */
final class AuthNormalizer {

    private AuthNormalizer() {
    }

    static String email(String email) {
        return email == null ? null : email.toLowerCase();
    }

    static String username(String username) {
        return username == null ? null : username.toLowerCase();
    }
}
