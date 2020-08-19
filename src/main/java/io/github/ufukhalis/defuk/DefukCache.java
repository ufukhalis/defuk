package io.github.ufukhalis.defuk;

import io.github.ufukhalis.defuk.adapter.DefukCacheAdapter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class DefukCache {

    public static <T, M> T fromCache(final Supplier<T> operation, final M key, final DefukCacheAdapter<M, T> adapter) {
        Optional<T> maybeResult = adapter.get(key);
        if (maybeResult.isPresent()) {
            return maybeResult.get();
        }

        T result = operation.get();
        adapter.put(key, result);

        return result;
    }


    public static <T, M> CompletableFuture<T> fromNonBlockingCache(final Supplier<CompletableFuture<T>> completableFutureSupplier,
                                                                   final M key,
                                                                   final DefukCacheAdapter<M, T> adapter) {

        Optional<T> maybeResult = adapter.get(key);

        return maybeResult.map(CompletableFuture::completedFuture)
                .orElseGet(() -> completableFutureSupplier.get()
                        .thenApplyAsync(value -> {
                            adapter.put(key, value);
                            return value;
                        })
                );

    }
}
