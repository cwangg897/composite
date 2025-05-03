package com.composite.cache;

import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CompositeCacheManager implements CacheManager {

    private final List<CacheManager> cacheManagers;
    private final UpdatableCacheManager updatableCacheManager;
    private final List<String> cacheNames;

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
        if (CacheGroup.isCompositeType(name)) {
            List<Cache> caches = new ArrayList<>();
            for (CacheManager manager : cacheManagers) {
                Cache cache = manager.getCache(name);
                if (cache != null) {
                    caches.add(cache);
                }
            }
            return new CompositeCache(caches, updatableCacheManager);
        }

        for (CacheManager manager : cacheManagers) {
            Cache cache = manager.getCache(name);
            if (cache != null) {
                return cache;
            }
        }
        return null;
    }

    @Override
    public List<String> getCacheNames() {
        return new ArrayList<>(cacheNames);
    }
}
