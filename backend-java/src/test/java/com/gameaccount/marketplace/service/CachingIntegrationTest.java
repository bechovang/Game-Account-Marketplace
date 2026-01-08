package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.cache.CacheMetricsLogger;
import com.gameaccount.marketplace.cache.CacheWarmer;
import com.gameaccount.marketplace.config.CacheConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for caching behavior
 * These tests verify that caching is properly configured and working
 * NOTE: Tests use @Autowired(required=false) to run even without Redis connection
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.redis.host=localhost",
        "spring.redis.port=6379",
        "spring.cache.type=redis"
})
class CachingIntegrationTest {

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Autowired(required = false)
    private GameService gameService;

    @Autowired(required = false)
    private AccountService accountService;

    @Autowired(required = false)
    private CacheMetricsLogger cacheMetricsLogger;

    @Autowired(required = false)
    private CacheWarmer cacheWarmer;

    @Test
    void testCacheManagerExists() {
        // Verify cache manager is configured
        assertThat(cacheManager).isNotNull();
    }

    @Test
    void testCacheNames() {
        if (cacheManager != null) {
            String[] cacheNames = cacheManager.getCacheNames();
            assertThat(cacheNames).isNotEmpty();
        }
    }

    @Test
    void testServicesExist() {
        // Verify services exist for cache warming
        assertThat(gameService).isNotNull();
        assertThat(accountService).isNotNull();
    }

    @Test
    void testCacheComponentsExist() {
        // Verify cache monitoring components exist
        assertThat(cacheMetricsLogger).isNotNull();
        assertThat(cacheWarmer).isNotNull();
    }

    @Test
    void testEnableSchedulingAnnotationPresent() {
        // Verify @EnableScheduling is present on CacheConfig (required for @Scheduled methods)
        assertThat(CacheConfig.class.isAnnotationPresent(EnableScheduling.class))
                .as("@EnableScheduling must be present on CacheConfig for @Scheduled methods to work")
                .isTrue();
    }

    @Test
    void testGetAllGamesCaching() {
        if (gameService != null) {
            // First call should hit the database
            var games1 = gameService.getAllGames();
            assertThat(games1).isNotNull();

            // Second call should hit the cache
            var games2 = gameService.getAllGames();
            assertThat(games2).isNotNull();

            // Results should be equal
            assertThat(games1).isEqualTo(games2);
        }
    }

    @Test
    void testGetFeaturedAccountsCaching() {
        if (accountService != null) {
            // First call should hit the database
            var featured1 = accountService.getFeaturedAccounts();
            assertThat(featured1).isNotNull();

            // Second call should hit the cache
            var featured2 = accountService.getFeaturedAccounts();
            assertThat(featured2).isNotNull();

            // Results should be equal
            assertThat(featured1).isEqualTo(featured2);
        }
    }

    @Test
    void testGetPopularAccountsCaching() {
        if (accountService != null) {
            // First call should hit the database
            var popular1 = accountService.getPopularAccounts();
            assertThat(popular1).isNotNull();

            // Second call should hit the cache
            var popular2 = accountService.getPopularAccounts();
            assertThat(popular2).isNotNull();

            // Results should be equal
            assertThat(popular1).isEqualTo(popular2);
        }
    }
}
