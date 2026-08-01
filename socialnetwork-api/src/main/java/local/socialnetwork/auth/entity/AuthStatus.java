package local.socialnetwork.auth.entity;

/**
 * Lifecycle state of an {@link AuthUser} account.
 */
public enum AuthStatus {
    PENDING_VERIFICATION,
    ACTIVE,
    DISABLED
}
