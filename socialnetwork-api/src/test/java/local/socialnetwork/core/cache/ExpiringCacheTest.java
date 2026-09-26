package local.socialnetwork.core.cache;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpiringCacheTest {

    @Test
    void get_reusesLoadedValueWithinTtl() {
        var loads = new AtomicInteger();
        var cache = new ExpiringCache<String, String>(Duration.ofMinutes(1), 10);

        var first = cache.get("k", key -> key + loads.incrementAndGet());
        var second = cache.get("k", key -> key + loads.incrementAndGet());

        assertEquals("k1", first);
        assertEquals("k1", second);
        assertEquals(1, loads.get());
    }

    @Test
    void get_reloadsAfterTtlExpires() throws InterruptedException {
        var loads = new AtomicInteger();
        var cache = new ExpiringCache<String, Integer>(Duration.ofMillis(20), 10);

        cache.get("k", key -> loads.incrementAndGet());
        Thread.sleep(50);
        var reloaded = cache.get("k", key -> loads.incrementAndGet());

        assertEquals(2, reloaded);
    }

    @Test
    void evictAndClear_forceReload() {
        var loads = new AtomicInteger();
        var cache = new ExpiringCache<String, Integer>(Duration.ofMinutes(1), 10);

        cache.get("a", key -> loads.incrementAndGet());
        cache.evict("a");
        cache.get("a", key -> loads.incrementAndGet());
        cache.clear();
        cache.get("a", key -> loads.incrementAndGet());

        assertEquals(3, loads.get());
    }

    @Test
    void get_whenLoaderThrows_doesNotCacheFailure() {
        var cache = new ExpiringCache<String, String>(Duration.ofMinutes(1), 10);

        assertThrows(IllegalStateException.class, () -> cache.get("k", key -> {
            throw new IllegalStateException("boom");
        }));

        assertEquals("ok", cache.get("k", key -> "ok"));
    }

    @Test
    void get_whenFull_staysBounded() {
        var loads = new AtomicInteger();
        var cache = new ExpiringCache<Integer, Integer>(Duration.ofMinutes(1), 2);

        for (var i = 0; i < 5; i++) {
            cache.get(i, key -> loads.incrementAndGet());
        }

        assertEquals(5, loads.get());
    }
}
