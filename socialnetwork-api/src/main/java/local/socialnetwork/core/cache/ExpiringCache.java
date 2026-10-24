package local.socialnetwork.core.cache;

import java.time.Duration;

import java.util.Objects;

import java.util.concurrent.ConcurrentHashMap;

import java.util.function.Function;

/**
 * A small thread-safe TTL cache with a hard size bound, for per-instance caches of cheap-to-recompute values
 * (authenticated principals, presigned URLs). Concurrent misses for the same key run the loader once.
 * Not a general-purpose cache: when full it drops expired entries first and then an arbitrary tenth of the rest.
 *
 * @param <K> key type
 * @param <V> value type, never {@code null}
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
     * Returns the cached value for {@code key}, loading and caching it if absent or expired. A loader that
     * throws leaves nothing cached.
     */
    public V get(K key, Function<? super K, ? extends V> loader) {
        var now = System.nanoTime();
        var entry = entries.get(key);
        if (entry != null && now - entry.expiresAtNanos() < 0) {
            return entry.value();
        }
        if (entries.size() >= maxSize) {
            makeRoom(now);
        }
        // compute() runs the loader under the key's bin lock, so concurrent misses load once.
        return entries.compute(key, (k, current) -> {
            if (current != null && now - current.expiresAtNanos() < 0) {
                return current;
            }
            var value = Objects.requireNonNull(loader.apply(k), "loader must not return null");
            return new Entry<>(value, System.nanoTime() + ttlNanos);
        }).value();
    }

    public void evict(K key) {
        entries.remove(key);
    }

    public void clear() {
        entries.clear();
    }

    private void makeRoom(long now) {
        entries.values().removeIf(e -> now - e.expiresAtNanos() >= 0);
        if (entries.size() < maxSize) {
            return;
        }
        var toDrop = Math.max(maxSize / 10, 1);
        var it = entries.keySet().iterator();
        while (toDrop-- > 0 && it.hasNext()) {
            it.next();
            it.remove();
        }
    }
}
