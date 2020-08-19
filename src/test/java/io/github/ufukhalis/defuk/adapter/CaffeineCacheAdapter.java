package io.github.ufukhalis.defuk.adapter;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.Optional;

public class CaffeineCacheAdapter<K, V> implements DefukCacheAdapter<K, V> {

    private final Cache<K, V> cache;

    public CaffeineCacheAdapter(Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

}
