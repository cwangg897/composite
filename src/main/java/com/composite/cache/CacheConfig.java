package com.composite.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    private final RedisConnectionFactory redisConnectionFactory;


    public CacheConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    /**
     * CacheType에 따른 TTL설정을 통해 Cache를 생성하고 이를바탕으로 LocalCacheManager 생성
     */
    @Bean
    public LocalCacheManager localCacheManager() {
        List<Cache> caches = Arrays.stream(CacheGroup.values())
            .filter(it -> it.getCacheType() == CacheType.LOCAL || it.getCacheType() == CacheType.COMPOSITE)
            .map(this::toCaffeineCache)
            .toList();

        return new LocalCacheManager(caches);
    }

    private Cache toCaffeineCache(CacheGroup group) {
        return new CaffeineCache(
            group.getCacheName(),
            Caffeine.newBuilder()
                .expireAfterWrite(group.getExpiredAfterWrite())
                .build()
        );
    }


//    @Bean
//    public CacheManager redisCacheManager(RedisSerializer<Object> redisSerializer) {
//        Map<String, RedisCacheConfiguration> configMap = Arrays.stream(CacheGroup.values())
//            .filter(it -> it.getCacheType() == CacheType.GLOBAL || it.getCacheType() == CacheType.COMPOSITE)
//            .collect(Collectors.toMap(
//                CacheGroup::getCacheName,
//                it -> RedisCacheConfiguration.defaultCacheConfig()
//                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
//                    .entryTtl(it.getExpiredAfterWrite())
//            ));
//
//        // CacheType에 따른 직렬화랑 TTL설정을 만들고 RedisCacheManager를 생성
//        return RedisCacheManager.builder(redisConnectionFactory)
//            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
//            .withInitialCacheConfigurations(configMap)
//            .enableStatistics()
//            .build();
//    }

    /**
     * 동적기반
     */
    @Bean(name = "redisCacheManager")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory, RedisSerializer<Object> serializer) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .enableStatistics()
            .build(); // default만 설정 → getCache(name) 호출 시 동적 생성 가능
    }

    @Bean
    @Primary
    public CacheManager compositeCacheManager(    @Qualifier("redisCacheManager") CacheManager redisCacheManager,
        LocalCacheManager localCacheManager) {
//        LocalCacheManager localCacheManager = localCacheManager();
        return new CompositeCacheManager(Arrays.asList(localCacheManager, redisCacheManager), localCacheManager);
    }


}
