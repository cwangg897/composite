package com.composite.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

@Slf4j
public class LocalCacheManager implements CacheManager, UpdatableCacheManager {


    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    public LocalCacheManager() {
    }

    public LocalCacheManager(List<Cache> caches) {
        for (Cache cache : caches) {
            cacheMap.put(cache.getName(), cache);
        }
    }

    @Override
    public Cache getCache(String name) {
        log.info("로컬 캐시 조회 : {}", name);
        return cacheMap.computeIfAbsent(name, this::createNewCache);
    }

    private Cache createNewCache(String name) {
        return new CaffeineCache(name, Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS) // default TTL
            .recordStats()
            .build());
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
