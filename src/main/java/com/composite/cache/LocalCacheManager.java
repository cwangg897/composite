package com.composite.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

public class LocalCacheManager  implements CacheManager, UpdatableCacheManager {

    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    public LocalCacheManager(List<Cache> caches) {
        for (Cache cache : caches) {
            cacheMap.put(cache.getName(), cache);
        }
    }

    private Cache createNewCache(String name) {
        return new CaffeineCache(name, Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES) // default TTL
            .build());
    }

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, this::createNewCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }

    @Override
    public void putIfAbsent(Cache cache, Object key, Object value) {
        Cache local = getCache(cache.getName());
        if (local != null) {
            local.putIfAbsent(key, value);
        }
    }
}
