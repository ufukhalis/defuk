package io.github.ufukhalis.defuk;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.ufukhalis.defuk.DefukCache;
import io.github.ufukhalis.defuk.adapter.CaffeineCacheAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DefukCacheTests {

    private Cache<String, Integer> cache = Caffeine.newBuilder()
            .maximumSize(5)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    private CaffeineCacheAdapter<String, Integer> adapter = new CaffeineCacheAdapter<>(cache);

    @Test
    void whenFromCache_thenShouldCacheValue() {
        String key = "defuk";
        Assertions.assertFalse(adapter.get(key).isPresent());

        Integer cacheResult = DefukCache.fromCache(() -> 1, key, adapter);

        Optional<Integer> cacheValue = adapter.get(key);

        Assertions.assertTrue(cacheValue.isPresent());
        Assertions.assertEquals(cacheResult, cacheValue.get());
    }


    @Test
    void whenFromNonBlockingCache_thenShouldCacheValue() throws ExecutionException, InterruptedException {
        String key = "defuk-nonblocking";
        Assertions.assertFalse(adapter.get(key).isPresent());

        AtomicInteger cacheHit = new AtomicInteger(0);
        AtomicInteger cacheMissed = new AtomicInteger(0);

        Integer cacheResult = 0;
        for (int i = 0; i < 2; i++) {
            cacheResult = DefukCache
                    .fromNonBlockingCache(() -> CompletableFuture.completedFuture(1), key, adapter)
                    .doOnCacheMiss(__ -> cacheMissed.incrementAndGet())
                    .doOnCacheHit(__ -> cacheHit.incrementAndGet())
                    .subscribe()
                    .get();
        }

        Optional<Integer> cacheValue = adapter.get(key);

        Assertions.assertTrue(cacheValue.isPresent());
        Assertions.assertEquals(cacheResult, cacheValue.get());
        Assertions.assertEquals(1, cacheHit.get());
        Assertions.assertEquals(1, cacheMissed.get());
    }
}
