package io.github.ufukhalis.defuk.adapter;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CaffeineNonBlockingCacheAdapter<K, V> implements DefukNonBlockingCacheAdapter<K, V> {

    private final Cache<K, V> cache;

    public CaffeineNonBlockingCacheAdapter(Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public CompletableFuture<Optional<V>> get(K key) {
        return CompletableFuture.completedFuture(Optional.ofNullable(cache.getIfPresent(key)));
    }

    @Override
    public CompletableFuture<V> put(K key, V value) {
        return CompletableFuture.supplyAsync(() -> {
            cache.put(key, value);
            return value;
        });
    }

}
