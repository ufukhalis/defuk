package io.github.ufukhalis.defuk.adapter;

import java.util.Optional;

public interface DefukCacheAdapter<K,V> {

    Optional<V> get(K key);

    void put(K key, V value);
}
