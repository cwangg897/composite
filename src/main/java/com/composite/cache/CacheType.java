package com.composite.cache;

public enum CacheType {
    LOCAL,     // L1 캐시 (ex: Caffeine)
    GLOBAL,    // L2 캐시 (ex: Redis)
    COMPOSITE  // L1 + L2 조합 캐시
}
