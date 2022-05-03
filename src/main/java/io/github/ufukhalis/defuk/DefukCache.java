package io.github.ufukhalis.defuk;

import io.github.ufukhalis.defuk.adapter.DefukCacheAdapter;
import io.github.ufukhalis.defuk.adapter.DefukNonBlockingCacheAdapter;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefukCache<K, V> {

    private final Supplier<CompletableFuture<V>> completableFutureSupplier;
    private final K key;
    private final DefukNonBlockingCacheAdapter<K, V> nonBlockingAdapter;

    private Consumer<? super V> onHit;
    private Consumer<K> onMiss;

    public static <T, M> T fromCache(final Supplier<T> operation, final M key, final DefukCacheAdapter<M, T> adapter) {
        Optional<T> maybeResult = adapter.get(key);
        if (maybeResult.isPresent()) {
            return maybeResult.get();
        }

        T result = operation.get();
        adapter.put(key, result);

        return result;
    }

    public DefukCache(Supplier<CompletableFuture<V>> completableFutureSupplier, K key, DefukNonBlockingCacheAdapter<K, V> adapter) {
        this.completableFutureSupplier = completableFutureSupplier;
        this.key = key;
        this.nonBlockingAdapter = adapter;
    }

    public static <T, M> DefukCache<T, M> fromNonBlockingCache(final Supplier<CompletableFuture<M>> completableFutureSupplier,
                                                               final T key,
                                                               final DefukNonBlockingCacheAdapter<T, M> adapter) {
        return new DefukCache<>(completableFutureSupplier, key, adapter);
    }

    public final DefukCache<K, V> doOnCacheHit(Consumer<? super V> onHit) {
        this.onHit = onHit;
        return this;
    }

    public final DefukCache<K, V> doOnCacheMiss(Consumer<K> onMiss) {
        this.onMiss = onMiss;
        return this;
    }

    public final CompletableFuture<V> subscribe() {
        return Objects.requireNonNull(nonBlockingAdapter).get(key)
                .thenCompose(cachedValue -> {
                    if (cachedValue.isPresent()) {
                        V value = cachedValue.get();
                        executeConsumerFunction(onHit, value);
                        return CompletableFuture.completedFuture(value);
                    }

                    return completableFutureSupplier.get()
                            .thenCompose(value -> {
                                executeConsumerFunction(onMiss, key);
                                return nonBlockingAdapter.put(key, value);
                            });
                });
    }

    private <T> T executeConsumerFunction(Consumer<? super T> consumer, T o) {
        if (consumer != null) {
            consumer.accept(o);
        }
        return o;
    }
}
