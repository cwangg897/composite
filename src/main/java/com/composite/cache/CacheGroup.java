//package com.composite.cache;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import lombok.Getter;
//
//@Getter
//public enum CacheGroup {
//    LOCAL_ONLY(CacheName.LOCAL_ONLY, Duration.ofMinutes(10), CacheType.LOCAL),
//    GLOBAL_ONLY(CacheName.GLOBAL_ONLY, Duration.ofMinutes(10), CacheType.GLOBAL),
//    COMPOSITE_ALL(CacheName.COMPOSITE, Duration.ofMinutes(10), CacheType.COMPOSITE);
//
//    private static final Map<String, CacheGroup> CACHE_MAP = new HashMap<>();
//
//    static {
//        for (CacheGroup group : values()) {
//            CACHE_MAP.put(group.cacheName, group);
//        }
//    }
//
//    private final String cacheName;
//    private final Duration expiredAfterWrite;
//    private final CacheType cacheType;
//
//    CacheGroup(String cacheName, Duration expiredAfterWrite, CacheType cacheType) {
//        this.cacheName = cacheName;
//        this.expiredAfterWrite = expiredAfterWrite;
//        this.cacheType = cacheType;
//    }
//
//    public static boolean isCompositeType(String cacheName) {
//        return get(cacheName).cacheType == CacheType.COMPOSITE;
//    }
//
//    private static CacheGroup get(String cacheName) {
//        CacheGroup cacheGroup = CACHE_MAP.get(cacheName);
//        if (cacheGroup == null) {
//            throw new NoSuchElementException(cacheName + " Cache Name Not Found");
//        }
//        return cacheGroup;
//    }
//}
