package com.composite.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocalCacheStatsController {


    private final CacheManager cacheManager;

    public LocalCacheStatsController(@Qualifier("localCacheManager") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/cache-stats/{cacheName}")
    public ResponseEntity<?> getCacheStats(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CaffeineCache caffeineCache) {
            com.github.benmanes.caffeine.cache.Cache<?, ?> nativeCache = caffeineCache.getNativeCache();
            CacheStats stats = nativeCache.stats();

            Map<String, Object> result = new HashMap<>();
            result.put("hitCount", stats.hitCount());
            result.put("missCount", stats.missCount());
            result.put("hitRate", stats.hitRate());
            result.put("evictionCount", stats.evictionCount());

            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Cache not found or not a CaffeineCache");
    }
}
