package com.composite.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;

public class CompositeCacheManager implements CacheManager {

    private final List<CacheManager> cacheManagers;
    private final UpdatableCacheManager updatableCacheManager;
    private final List<String> cacheNames;
    private final ConcurrentHashMap<String, Cache> cacheLookupCache = new ConcurrentHashMap<>();

    public CompositeCacheManager(List<CacheManager> cacheManagers, UpdatableCacheManager updatableCacheManager) {
        this.cacheManagers = cacheManagers;
        this.updatableCacheManager = updatableCacheManager;
        this.cacheNames = new ArrayList<>();
        for (CacheManager manager : cacheManagers) {
            this.cacheNames.addAll(manager.getCacheNames());
        }
    }
    
    @Override
    public Cache getCache(String name) {
        return cacheLookupCache.computeIfAbsent(name, this::buildCompositeCache);
    }

    /**
     * 캐시가 없으면 빈 캐시라도 반환
     * @param name
     * @return
     */
    private Cache buildCompositeCache(String name) {
        List<Cache> caches = cacheManagers.stream()
            .map(manager -> manager.getCache(name))
            .filter(Objects::nonNull)
            .toList();

        if (caches.isEmpty()) {
            return new NoOpCache(name); // 캐시가 없으면 빈 캐시라도 반환
        }

        return new CompositeCache(caches, updatableCacheManager);
    }




//    @Override
//    public Cache getCache(String name) {
//        List<Cache> caches = new ArrayList<>();
//        for (CacheManager manager : cacheManagers) {
//            Cache cache = manager.getCache(name);
//            if (cache != null) {
//                caches.add(cache);
//            }
//        }
//        // 최소 하나라도 있으면 CompositeCache 생성
//        if (!caches.isEmpty()) {
//            return new CompositeCache(caches, updatableCacheManager); // todo. CompositeCache를 매번만드는게 문제임
//        }
//        return null;
//    }

    @Override
    public List<String> getCacheNames() {
        return new ArrayList<>(cacheNames);
    }
}
