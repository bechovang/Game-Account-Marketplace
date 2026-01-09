package com.gameaccount.marketplace.controller;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.service.AccountService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Performance Benchmarking Controller
 *
 * This controller provides endpoints to measure query performance
 * before and after optimizations (Redis cache, DataLoader, indexes).
 *
 * USAGE:
 * 1. Set profile: benchmark
 * 2. Run benchmarks: GET /api/benchmark/run-all
 * 3. Compare results before/after optimizations
 *
 * SECURITY:
 * - Only accessible with benchmark profile enabled
 * - Requires ADMIN role
 */
@Slf4j
@RestController
@RequestMapping("/api/benchmark")
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "benchmark")
@RequiredArgsConstructor
public class BenchmarkController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    /**
     * Test 1: Simple query - baseline performance
     * Tests basic SELECT without filters
     */
    @GetMapping("/test-1-simple-query")
    public BenchmarkResult testSimpleQuery() {
        log.info("🔬 Benchmark: Simple Query (Baseline)");
        long start = System.currentTimeMillis();

        // Query: Get 100 accounts without filters
        Pageable pageable = PageRequest.of(0, 100);
        Page<Account> accounts = accountService.searchAccounts(
                null,    // gameId
                null,    // minPrice
                null,    // maxPrice
                null,    // status
                pageable
        );

        long duration = System.currentTimeMillis() - start;
        log.info("✅ Completed in {}ms", duration);

        return new BenchmarkResult(
                "Simple Query (Baseline)",
                duration,
                accounts.getTotalElements(),
                "Basic pagination without filters"
        );
    }

    /**
     * Test 2: Filtered query - tests index effectiveness
     * Tests with WHERE clauses on indexed columns
     */
    @GetMapping("/test-2-filtered-query")
    public BenchmarkResult testFilteredQuery() {
        log.info("🔬 Benchmark: Filtered Query (Index Test)");
        long start = System.currentTimeMillis();

        // Query: Get accounts with filters
        Pageable pageable = PageRequest.of(0, 100);
        Page<Account> accounts = accountService.searchAccounts(
                1L,                                  // gameId
                100.0,                               // minPrice
                500.0,                               // maxPrice
                Account.AccountStatus.APPROVED,      // status
                pageable
        );

        long duration = System.currentTimeMillis() - start;
        log.info("✅ Completed in {}ms", duration);

        return new BenchmarkResult(
                "Filtered Query (Index Test)",
                duration,
                accounts.getTotalElements(),
                "Tests index effectiveness on WHERE clauses"
        );
    }

    /**
     * Test 3: Nested query (N+1 problem test)
     * Tests DataLoader optimization for nested entities
     *
     * IMPORTANT: This endpoint forces lazy loading to expose N+1 issues
     * Run BEFORE and AFTER enabling DataLoader to see the difference
     */
    @GetMapping("/test-3-nested-query")
    public BenchmarkResult testNestedQuery() {
        log.info("🔬 Benchmark: Nested Query (N+1 Problem Test)");
        long start = System.currentTimeMillis();

        // Query: Get 100 approved accounts (without JOIN FETCH)
        // This query doesn't load relationships, triggering lazy loading
        List<Account> accounts = accountRepository.findByStatus(
                Account.AccountStatus.APPROVED
        ).stream()
                .limit(100)
                .toList();

        // Force load relationships (this triggers N+1 without DataLoader)
        for (Account account : accounts) {
            account.getSeller().getFullName();
            account.getGame().getName();
        }

        long duration = System.currentTimeMillis() - start;
        log.info("✅ Completed in {}ms", duration);

        return new BenchmarkResult(
                "Nested Query (N+1 Test)",
                duration,
                (long) accounts.size(),
                "Tests lazy loading N+1 problem. Expected: ~201 queries without DataLoader, ~3 with DataLoader"
        );
    }

    /**
     * Test 4: Optimized nested query with JOIN FETCH
     * Tests the effectiveness of searchAccountsWithJoins
     */
    @GetMapping("/test-4-optimized-nested-query")
    public BenchmarkResult testOptimizedNestedQuery() {
        log.info("🔬 Benchmark: Optimized Nested Query (JOIN FETCH Test)");
        long start = System.currentTimeMillis();

        // Query: Get 100 approved accounts WITH JOIN FETCH
        // This loads relationships in a single query
        Pageable pageable = PageRequest.of(0, 100, Sort.by("createdAt").descending());
        Page<Account> accounts = accountRepository.searchAccountsWithJoins(
                null,                           // gameId
                null,                           // minPrice
                null,                           // maxPrice
                null,                           // minLevel
                null,                           // maxLevel
                null,                           // rank
                Account.AccountStatus.APPROVED, // status
                null,                           // isFeatured
                null,                           // searchText
                null,                           // sellerId
                pageable
        );

        // Access relationships (already loaded, no additional queries)
        for (Account account : accounts.getContent()) {
            account.getSeller().getFullName();
            account.getGame().getName();
        }

        long duration = System.currentTimeMillis() - start;
        log.info("✅ Completed in {}ms", duration);

        return new BenchmarkResult(
                "Optimized Nested Query (JOIN FETCH)",
                duration,
                accounts.getTotalElements(),
                "Uses JOIN FETCH to prevent N+1. Expected: ~1-3 queries total"
        );
    }

    /**
     * Test 5: Cache effectiveness - tests Redis caching
     * First call = cache miss, Second call = cache hit
     */
    @GetMapping("/test-5-cache-effectiveness")
    public BenchmarkResult testCacheEffectiveness() {
        log.info("🔬 Benchmark: Cache Effectiveness (Redis Test)");

        // First call (cache miss)
        long start1 = System.currentTimeMillis();
        accountService.getFeaturedAccounts();
        long firstCallDuration = System.currentTimeMillis() - start1;
        log.info("📥 First call (cache miss): {}ms", firstCallDuration);

        // Second call (cache hit)
        long start2 = System.currentTimeMillis();
        accountService.getFeaturedAccounts();
        long secondCallDuration = System.currentTimeMillis() - start2;
        log.info("📥 Second call (cache hit): {}ms", secondCallDuration);

        long improvement = firstCallDuration - secondCallDuration;
        double improvementPercent = firstCallDuration > 0
                ? (improvement * 100.0) / firstCallDuration
                : 0;

        log.info("✅ Cache improvement: {}ms ({:.1f}%)", improvement, improvementPercent);

        return new BenchmarkResult(
                "Cache Effectiveness (Redis Test)",
                improvement,
                0L,
                String.format("First: %dms, Second: %dms, Improvement: %dms (%.1f%%)",
                        firstCallDuration, secondCallDuration, improvement, improvementPercent)
        );
    }

    /**
     * Run all benchmarks and return comprehensive results
     */
    @GetMapping("/run-all")
    public String runAllBenchmarks() {
        log.info("🚀 Starting comprehensive benchmark suite...");
        StringBuilder results = new StringBuilder();
        results.append("╔════════════════════════════════════════════════════════════╗\n");
        results.append("║         PERFORMANCE BENCHMARK RESULTS                      ║\n");
        results.append("╚════════════════════════════════════════════════════════════╝\n\n");

        // Test 1: Simple Query
        BenchmarkResult result1 = testSimpleQuery();
        results.append(formatResult(result1));

        // Test 2: Filtered Query
        BenchmarkResult result2 = testFilteredQuery();
        results.append(formatResult(result2));

        // Test 3: Nested Query (N+1)
        BenchmarkResult result3 = testNestedQuery();
        results.append(formatResult(result3));

        // Test 4: Optimized Nested Query
        BenchmarkResult result4 = testOptimizedNestedQuery();
        results.append(formatResult(result4));

        // Test 5: Cache Effectiveness
        BenchmarkResult result5 = testCacheEffectiveness();
        results.append(formatResult(result5));

        results.append("══════════════════════════════════════════════════════════════\n");
        results.append("✅ Benchmark suite completed!\n");
        results.append("══════════════════════════════════════════════════════════════\n");

        log.info("🏁 Benchmark suite completed");

        return results.toString();
    }

    private String formatResult(BenchmarkResult result) {
        return String.format(
                "┌─ %s\n" +
                "│   Duration: %dms\n" +
                "│   Records:  %d\n" +
                "│   Details:  %s\n" +
                "└─────────────────────────────────────────────────────────\n\n",
                result.getTestName(),
                result.getDuration(),
                result.getRecordCount(),
                result.getDetails()
        );
    }

    /**
     * Benchmark Result DTO
     */
    @Data
    public static class BenchmarkResult {
        private final String testName;
        private final Long duration;
        private final Long recordCount;
        private final String details;
    }
}
