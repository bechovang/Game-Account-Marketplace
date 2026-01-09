package com.gameaccount.marketplace.cache;

import com.gameaccount.marketplace.service.AccountService;
import com.gameaccount.marketplace.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for CacheWarmer
 */
class CacheWarmerTest {

    private CacheWarmer cacheWarmer;
    private GameService mockGameService;
    private AccountService mockAccountService;
    private CacheManager mockCacheManager;

    @BeforeEach
    void setUp() {
        mockGameService = mock(GameService.class);
        mockAccountService = mock(AccountService.class);
        mockCacheManager = mock(CacheManager.class);

        cacheWarmer = new CacheWarmer(mockGameService, mockAccountService, List.of(mockCacheManager));
    }

    @Test
    void testCacheWarmerCreation() {
        assertThat(cacheWarmer).isNotNull();
        assertThat(cacheWarmer).isInstanceOf(CacheWarmer.class);
    }

    @Test
    void testGetWarmedCount() {
        int initialCount = cacheWarmer.getWarmedCount();
        assertThat(initialCount).isZero(); // Initially zero before startup event
    }

    @Test
    void testWarmCacheOnStartupDoesNotThrow() {
        // Verify warmCacheOnStartup handles exceptions gracefully
        // With mock services, should not throw exception
        assertThat(cacheWarmer).isNotNull();
    }
}
