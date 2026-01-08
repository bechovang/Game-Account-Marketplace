package com.gameaccount.marketplace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;

/**
 * Cache configuration for Redis caching with fallback to in-memory cache.
 * Provides cache manager with 10-minute TTL for account search results.
 * Falls back to in-memory cache if Redis is not available.
 * Includes scheduled periodic cache eviction to ensure data freshness.
 */
@Configuration
@EnableCaching
@EnableScheduling
@Slf4j
public class CacheConfig {

    /**
     * Configure Redis cache manager with default settings (when Redis is available)
     *
     * @param factory Redis connection factory
     * @return Configured Redis cache manager
     */
    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory factory) {
        log.info("Configuring Redis cache manager with 10-minute TTL");

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * Fallback in-memory cache manager (when Redis is NOT available)
     * This allows development without Redis while maintaining caching behavior
     *
     * @return In-memory cache manager
     */
    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public CacheManager fallbackCacheManager() {
        log.warn("Redis not available - using in-memory cache manager as fallback (development mode)");
        return new ConcurrentMapCacheManager("accounts");
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
