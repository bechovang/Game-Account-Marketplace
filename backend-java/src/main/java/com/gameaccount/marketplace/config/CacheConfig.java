package com.gameaccount.marketplace.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for Redis caching with fallback to Caffeine and in-memory cache.
 * Provides cache manager with per-cache TTL configurations and JSON serialization.
 * Falls back to Caffeine (if Redis unavailable) then in-memory cache.
 * Includes scheduled periodic cache eviction to ensure data freshness.
 * Enables cache statistics for monitoring cache performance.
 */
@Configuration
@EnableCaching
@EnableScheduling
@Slf4j
public class CacheConfig {

    /**
     * Configure Redis cache manager with JSON serialization and per-cache TTL (when Redis is available)
     *
     * @param factory Redis connection factory
     * @return Configured Redis cache manager
     */
    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory factory) {
        log.info("Configuring Redis cache manager with GenericJackson2JsonRedisSerializer and per-cache TTL");

        // Configure JSON serialization instead of Java serialization
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));
                // .enableStatistics(); // Enable cache statistics for monitoring - disabled for compatibility

        // Per-cache TTL configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("accounts", defaultConfig.entryTtl(Duration.ofMinutes(10))); // 10 minutes
        cacheConfigurations.put("games", defaultConfig.entryTtl(Duration.ofHours(1))); // 1 hour
        cacheConfigurations.put("featured", defaultConfig.entryTtl(Duration.ofMinutes(5))); // 5 minutes
        cacheConfigurations.put("favorites", defaultConfig.entryTtl(Duration.ofMinutes(10))); // 10 minutes

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * Secondary fallback: Caffeine cache manager (when Redis is NOT available)
     * This provides better performance than ConcurrentMapCache with expiration policies
     * Marked as @Primary to ensure Spring caching infrastructure has a unique CacheManager
     *
     * @return Caffeine cache manager
     */
    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    @Order(2)
    @Primary
    public CacheManager caffeineCacheManager() {
        log.warn("Redis not available - using Caffeine cache manager as fallback");

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
        );
        return cacheManager;
    }

    /**
     * Tertiary fallback: In-memory cache manager (when both Redis and Caffeine unavailable)
     * This is the final fallback for development environments
     *
     * @return In-memory cache manager
     */
    @Bean
    @ConditionalOnMissingBean({RedisConnectionFactory.class, CaffeineCacheManager.class})
    public CacheManager fallbackCacheManager() {
        log.warn("Redis and Caffeine not available - using in-memory cache manager as final fallback (development mode)");
        return new ConcurrentMapCacheManager("accounts", "games", "featured", "favorites");
    }

    /**
     * Scheduled periodic cache eviction to ensure data freshness.
     * Evicts all entries from the "accounts" cache every 10 minutes.
     * This works in conjunction with the TTL settings to provide cache consistency.
     */
    @CacheEvict(value = "accounts", allEntries = true)
    @Scheduled(fixedRate = 600000) // Every 10 minutes (600,000 ms)
    public void evictAllAccountsCache() {
        log.debug("Periodic cache eviction executed for 'accounts' cache");
    }
}
