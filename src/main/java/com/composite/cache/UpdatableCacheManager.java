package com.composite.cache;

import org.springframework.cache.Cache;

// ✅ 꼭 Caffeine만 있는게 아니니까 다른 로컬캐시도 있기때문에
public interface UpdatableCacheManager {
    void putIfAbsent(Cache cache, Object key, Object value);
}
