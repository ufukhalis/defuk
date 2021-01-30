package io.github.ufukhalis.defuk;

import io.github.ufukhalis.defuk.adapter.DefukCacheAdapter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefukCache<K, V> {

    private final Supplier<CompletableFuture<V>> completableFutureSupplier;
    private final K key;
    private final DefukCacheAdapter<K, V> adapter;

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

    public DefukCache(Supplier<CompletableFuture<V>> completableFutureSupplier, K key, DefukCacheAdapter<K, V> adapter) {
        this.completableFutureSupplier = completableFutureSupplier;
        this.key = key;
        this.adapter = adapter;
    }

    public static <T, M> DefukCache<T, M> fromNonBlockingCache(final Supplier<CompletableFuture<M>> completableFutureSupplier,
                                                               final T key,
                                                               final DefukCacheAdapter<T, M> adapter) {
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
        Optional<V> maybeResult = adapter.get(key);

        return maybeResult
                .map(__ -> executeConsumerFunction(onHit, __))
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> completableFutureSupplier.get()
                        .thenApplyAsync(value -> {
                            executeConsumerFunction(onMiss, key);
                            adapter.put(key, value);
                            return value;
                        })
                );
    }

    private <T> T executeConsumerFunction(Consumer<? super T> consumer, T o) {
        if (consumer != null) {
            consumer.accept(o);
        }
        return o;
    }
}
