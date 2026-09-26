package local.socialnetwork.core.cache;

import java.time.Duration;

import java.util.Objects;

import java.util.concurrent.ConcurrentHashMap;

import java.util.function.Function;

/**
 * Minimal in-process cache with a fixed time-to-live and a soft size bound.
 *
 * <p>Intended for small, hot, per-instance lookups (e.g. the authenticated principal or a presigned URL)
 * where a network round trip to Redis would cost as much as the work being cached. Values are never
 * {@code null}; loader exceptions propagate and nothing is cached for them. Concurrent misses on the same
 * key may each invoke the loader, which is acceptable for idempotent lookups.
 *
 * @param <K> key type
 * @param <V> value type
 */
public final class ExpiringCache<K, V> {

    private record Entry<V>(V value, long expiresAtNanos) {
    }

    private final ConcurrentHashMap<K, Entry<V>> entries = new ConcurrentHashMap<>();
    private final long ttlNanos;
    private final int maxSize;

    public ExpiringCache(Duration ttl, int maxSize) {
        this.ttlNanos = ttl.toNanos();
        this.maxSize = maxSize;
    }

    /**
     * Returns the cached value for {@code key}, loading and caching it when absent or expired.
     */
    public V get(K key, Function<? super K, ? extends V> loader) {
        var now = System.nanoTime();
        var entry = entries.get(key);
        if (entry != null && now - entry.expiresAtNanos() < 0) {
            return entry.value();
        }
        var value = Objects.requireNonNull(loader.apply(key), "loader must not return null");
        if (entries.size() >= maxSize) {
            entries.values().removeIf(e -> now - e.expiresAtNanos() >= 0);
            if (entries.size() >= maxSize) {
                entries.clear();
            }
        }
        entries.put(key, new Entry<>(value, now + ttlNanos));
        return value;
    }

    public void evict(K key) {
        entries.remove(key);
    }

    public void clear() {
        entries.clear();
    }
}
