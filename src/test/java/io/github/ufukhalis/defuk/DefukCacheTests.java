package io.github.ufukhalis.defuk;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.ufukhalis.defuk.adapter.CaffeineCacheAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
        String key = "defuk";
        Assertions.assertFalse(adapter.get(key).isPresent());

        Integer cacheResult = DefukCache
                .fromNonBlockingCache(() -> CompletableFuture.completedFuture(1), key, adapter).get();

        Optional<Integer> cacheValue = adapter.get(key);

        Assertions.assertTrue(cacheValue.isPresent());
        Assertions.assertEquals(cacheResult, cacheValue.get());
    }
}
