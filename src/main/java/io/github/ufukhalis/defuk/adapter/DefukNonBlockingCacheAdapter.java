package io.github.ufukhalis.defuk.adapter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DefukNonBlockingCacheAdapter<K,V> {

    CompletableFuture<Optional<V>> get(K key);

    CompletableFuture<V> put(K key, V value);
}
