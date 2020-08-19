DEFUK
=======

`DEFUK` is a simple periodic job runner and cache library which only needs Java 8+ version.


How to Use
-------------

Firstly, you should add latest `DEFUK` dependency to your project.

```$xslt
<dependency>
    <groupId>io.github.ufukhalis</groupId>
    <artifactId>defuk</artifactId>
    <version>0.0.1</version>
</dependency>
```

To run a periodic job first you need to build `JobConfig` like below.

```$xslt
private final JobConfig<Integer> jobConfig = new JobConfig.Builder<Integer>()
            .withInitialDelay(0L)
            .withPeriod(1L)
            .withThreadPoolSize(5) // Default value is 1
            .withOperation(() -> 1) // The operation which you want to run periodically
            .withTimeUnit(TimeUnit.SECONDS)
            .build();
```

After `JobConfig` definition, you can create the periodic job like below.

```$xslt
DefukJob<Integer> defukJob = DefukJob.periodic(jobConfig);
defukJob.start(); // This is required to run job

Optional<Integer> result = defukJob.get();

```

To make cache a method call, you can use the following code.

```$xslt
public Integer yourMethod() {
    // some long process
    return value;
}

Integer value = DefukCache.fromCache(() -> yourMethod(), "key", adapter);

```  

For non blocking way, you can do the following.

```$xslt
public CompletableFuture<Integer> yourMethod() {
    // some long process
    return value;
}

CompletableFuture<Integer> value = DefukCache.fromNonBlockingCache(() -> yourMethod(), "key", adapter)

```

When `Defuk.fromCache` and `Defuk.fromNonBlockingCache` methods are called many times with same parameters 
the caching will be valid. Regarding `Adapter` case, you need to create your adapter cache with your 
favorite in-memory cache library.

In this example, Caffeine is used.

```$xslt
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

```

Then you can create an instance like below.

```$xslt
Cache<String, Integer> cache = Caffeine.newBuilder()
            .maximumSize(5)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();
            
CaffeineCacheAdapter<String, Integer> adapter = new CaffeineCacheAdapter<>(cache);

```

License
------------
All code in this repository is licensed under the Apache License, Version 2.0. See [LICENCE](./LICENSE).