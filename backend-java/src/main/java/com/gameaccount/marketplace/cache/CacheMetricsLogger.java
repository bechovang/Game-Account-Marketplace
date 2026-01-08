package com.gameaccount.marketplace.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Cache metrics logger for monitoring cache performance.
 * Logs cache statistics (hits, misses, hit rate) every 5 minutes.
 * Works with RedisCacheManager when statistics are enabled.
 */
@Component
@Slf4j
public class CacheMetricsLogger {

    private final CacheManager cacheManager;

    /**
     * Constructor with autowired CacheManager.
     * Uses List to handle multiple CacheManager beans gracefully.
     * If multiple exist, uses the first one (should be @Primary).
     */
    @Autowired
    public CacheMetricsLogger(List<CacheManager> cacheManagers) {
        if (cacheManagers.isEmpty()) {
            log.warn("No CacheManager beans found - CacheMetricsLogger will be disabled");
            this.cacheManager = null;
        } else if (cacheManagers.size() > 1) {
            log.warn("Multiple CacheManager beans found ({}). Using the first one: {}",
                cacheManagers.size(), cacheManagers.get(0).getClass().getSimpleName());
            this.cacheManager = cacheManagers.get(0);
        } else {
            this.cacheManager = cacheManagers.get(0);
            log.info("CacheMetricsLogger initialized with: {}", cacheManager.getClass().getSimpleName());
        }
    }

    /**
     * Log cache statistics every 5 minutes.
     * Outputs hits, misses, and hit rate for each cache (where available).
     * Format: "Cache [name] - Hits: X, Misses: Y, Hit Rate: Z%"
     * NOTE: Redis statistics require Micrometer integration; Caffeine provides built-in stats.
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void logCacheStatistics() {
        if (cacheManager == null) {
            log.debug("CacheMetricsLogger disabled - no CacheManager available");
            return;
        }

        log.info("=== Cache Statistics ===");

        try {
            // Check if CacheManager supports statistics
            if (cacheManager instanceof org.springframework.data.redis.cache.RedisCacheManager redisCacheManager) {
                logCacheManagerStatistics(redisCacheManager);
            } else if (cacheManager instanceof org.springframework.cache.caffeine.CaffeineCacheManager caffeineCacheManager) {
                logCaffeineStatistics(caffeineCacheManager);
            } else if (cacheManager instanceof org.springframework.cache.concurrent.ConcurrentMapCacheManager mapCacheManager) {
                log.info("Using ConcurrentMapCacheManager (in-memory) - statistics not available");
            } else {
                log.info("Cache manager type: {} - statistics not available", cacheManager.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.warn("Failed to retrieve cache statistics: {}", e.getMessage());
        }

        log.info("=======================");
    }

    /**
     * Log statistics for RedisCacheManager
     * NOTE: Redis statistics are tracked but require Micrometer or custom instrumentation
     * to expose hit/miss counts. This logs the cache names and TTL configuration.
     */
    private void logCacheManagerStatistics(org.springframework.data.redis.cache.RedisCacheManager redisCacheManager) {
        // Get cache names
        var cacheNames = redisCacheManager.getCacheNames();

        for (String cacheName : cacheNames) {
            var cache = redisCacheManager.getCache(cacheName);
            if (cache != null) {
                var nativeCache = cache.getNativeCache();

                // RedisCache statistics require Micrometer metrics integration
                // Log cache existence and type - use Redis CLI or monitoring tools for detailed stats
                log.info("Cache [{}] - Type: Redis, TTL: Configured in CacheConfig", cacheName);
            }
        }

        log.info("Total caches managed: {}", cacheNames.size());
        log.info("Redis Statistics: Use 'redis-cli INFO STATS' or monitoring tools for hit/miss rates");
    }

    /**
     * Log statistics for CaffeineCacheManager
     */
    private void logCaffeineStatistics(org.springframework.cache.caffeine.CaffeineCacheManager caffeineCacheManager) {
        var cacheNames = caffeineCacheManager.getCacheNames();

        for (String cacheName : cacheNames) {
            var cache = caffeineCacheManager.getCache(cacheName);
            if (cache != null) {
                var nativeCache = cache.getNativeCache();
                if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache) {
                    var stats = caffeineCache.stats();
                    long hits = stats.hitCount();
                    long misses = stats.missCount();
                    double hitRate = stats.hitRate();

                    log.info("Cache [{}] - Hits: {}, Misses: {}, Hit Rate: {:.2f}%",
                            cacheName, hits, misses, hitRate * 100);
                }
            }
        }

        log.info("Total caches managed: {}", cacheNames.size());
    }

    /**
     * Log summary of all cache names managed by this cache manager
     */
    @Scheduled(fixedRate = 60000) // Every minute (separate from detailed stats)
    public void logCacheNames() {
        if (cacheManager == null) {
            return;
        }
        var cacheNames = cacheManager.getCacheNames();
        log.debug("Active caches: {}", String.join(", ", cacheNames));
    }
}
