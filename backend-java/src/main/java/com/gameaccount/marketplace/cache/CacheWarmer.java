package com.gameaccount.marketplace.cache;

import com.gameaccount.marketplace.service.AccountService;
import com.gameaccount.marketplace.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cache warmer component for pre-populating cache on application startup.
 * Loads hot data (games, featured accounts) into cache to improve initial performance.
 * Uses ApplicationReadyEvent to trigger after application context is fully initialized.
 */
@Component
@Slf4j
public class CacheWarmer {

    private final GameService gameService;
    private final AccountService accountService;
    private final CacheManager cacheManager;

    private final AtomicInteger warmedCount = new AtomicInteger(0);

    /**
     * Constructor with autowired dependencies.
     * Uses List to handle multiple CacheManager beans gracefully.
     */
    @Autowired
    public CacheWarmer(GameService gameService, AccountService accountService, List<CacheManager> cacheManagers) {
        this.gameService = gameService;
        this.accountService = accountService;
        if (cacheManagers.isEmpty()) {
            this.cacheManager = null;
        } else {
            this.cacheManager = cacheManagers.get(0);
        }
    }

    /**
     * Warm cache on application startup.
     * Pre-loads games and featured accounts into cache.
     * Logs progress and counts of warmed entries.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmCacheOnStartup() {
        log.info("=== Starting Cache Warming ===");

        warmedCount.set(0);

        try {
            // Warm games cache (1-hour TTL, rarely changes)
            warmGamesCache();

            // Warm featured accounts cache (5-minute TTL)
            warmFeaturedAccountsCache();

            // Warm popular accounts cache (5-minute TTL)
            warmPopularAccountsCache();

            log.info("=== Cache Warming Complete ===");
            log.info("Total caches warmed: {}", warmedCount.get());
            if (cacheManager != null) {
                log.info("Active caches: {}", String.join(", ", cacheManager.getCacheNames()));
            }

        } catch (Exception e) {
            log.error("Error during cache warming: {}", e.getMessage(), e);
            // Continue application startup even if cache warming fails
        }
    }

    /**
     * Warm games cache by calling getAllGames()
     */
    private void warmGamesCache() {
        try {
            log.info("Warming games cache...");
            var games = gameService.getAllGames();
            log.info("✓ Warmed games cache with {} entries", games.size());
            warmedCount.incrementAndGet();
        } catch (Exception e) {
            log.warn("Failed to warm games cache: {}", e.getMessage());
        }
    }

    /**
     * Warm featured accounts cache by calling getFeaturedAccounts()
     */
    private void warmFeaturedAccountsCache() {
        try {
            log.info("Warming featured accounts cache...");
            var featuredAccounts = accountService.getFeaturedAccounts();
            log.info("✓ Warmed featured accounts cache with {} entries", featuredAccounts.size());
            warmedCount.incrementAndGet();
        } catch (Exception e) {
            log.warn("Failed to warm featured accounts cache: {}", e.getMessage());
        }
    }

    /**
     * Warm popular accounts cache by calling getPopularAccounts()
     */
    private void warmPopularAccountsCache() {
        try {
            log.info("Warming popular accounts cache...");
            var popularAccounts = accountService.getPopularAccounts();
            log.info("✓ Warmed popular accounts cache with {} entries", popularAccounts.size());
            warmedCount.incrementAndGet();
        } catch (Exception e) {
            log.warn("Failed to warm popular accounts cache: {}", e.getMessage());
        }
    }

    /**
     * Get count of caches warmed during startup
     *
     * @return Number of caches warmed
     */
    public int getWarmedCount() {
        return warmedCount.get();
    }
}
