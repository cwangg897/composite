package com.composite.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
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
    private final ObjectMapper objectMapper;



    public CacheConfig(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.objectMapper = objectMapper;
    }

    /**
     * Local 캐시매니저
     */
    @Bean(name = "localCacheManager")
    public LocalCacheManager localCacheManager() {
        return new LocalCacheManager();
    }

    /**
     * 동적기반 Global캐시매니저
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
    public CacheManager compositeCacheManager(@Qualifier("redisCacheManager") CacheManager redisCacheManager,
        LocalCacheManager localCacheManager) {
        // 일부러 localManager를 앞으로 l1 -> l2 순서보장
        return new CompositeCacheManager(Arrays.asList(localCacheManager, redisCacheManager), localCacheManager);
    }


}
