package local.socialnetwork.auth.service.impl;

import local.socialnetwork.core.cache.ExpiringCache;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;

import java.util.function.Function;

/**
 * Short-lived, per-instance cache of authenticated {@link UserPrincipal}s keyed by username.
 *
 * <p>Avoids a database round trip on every authenticated request. A deleted account stays
 * authenticated for at most the configured TTL on other instances; on this instance it is evicted
 * as soon as the deleting transaction commits.
 */
@Component
public class UserPrincipalCache {

    private static final int MAX_ENTRIES = 10_000;

    private final ExpiringCache<String, UserPrincipal> cache;

    public UserPrincipalCache(@Value("${socialnetwork.security.principal-cache.ttl}") Duration ttl) {
        this.cache = new ExpiringCache<>(ttl, MAX_ENTRIES);
    }

    public UserPrincipal get(String username, Function<String, UserPrincipal> loader) {
        return cache.get(username, loader);
    }

    /**
     * Drops every cached principal. Intended for tests that recreate users between cases.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Evicts the username immediately, or once the surrounding transaction commits when one is active.
     */
    public void evictAfterCommit(String username) {
        if (username == null) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    cache.evict(username);
                }
            });
        } else {
            cache.evict(username);
        }
    }
}
