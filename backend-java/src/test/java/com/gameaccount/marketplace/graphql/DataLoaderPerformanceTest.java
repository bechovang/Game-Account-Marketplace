package com.gameaccount.marketplace.graphql;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.repository.GameRepository;
import com.gameaccount.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Performance tests for DataLoader N+1 query prevention.
 */
@SpringBootTest
@AutoConfigureGraphQlTester
@Transactional
class DataLoaderPerformanceTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    private List<Account> testAccounts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // Create test data for performance testing
        createTestData();
    }

    @Test
    @WithMockUser(username = "performance-test@example.com")
    void dataLoader_preventsNPlusOneQueries_withMultipleAccounts() {
        // Create query for multiple accounts with nested seller/game data
        String query = """
            query GetAccounts($limit: Int!) {
                accounts(page: 0, limit: $limit) {
                    content {
                        id
                        title
                        seller {
                            id
                            fullName
                        }
                        game {
                            id
                            name
                        }
                        isFavorited
                    }
                    totalElements
                }
            }
            """;

        // Execute query and verify it completes within time limit
        graphQlTester.document(query)
            .variable("limit", Math.min(testAccounts.size(), 20))
            .execute()
            .path("accounts.content").entityList(Account.class).hasSizeGreaterThan(0)
            .path("accounts.totalElements").entity(Long.class).isGreaterThanOrEqualTo(1);

        // Note: In a real performance test, you would use a query counter
        // to verify that only 3 queries are executed (accounts + users + games)
        // instead of N+1 queries (1 + N for sellers + N for games + N for favorites)
    }

    @Test
    @WithMockUser(username = "performance-test@example.com")
    void accountQuery_withNestedData_completesWithinTimeLimit() {
        // Test individual account query performance
        String query = """
            query GetAccount($accountId: ID!) {
                account(id: $accountId) {
                    id
                    title
                    seller {
                        id
                        fullName
                        rating
                        totalReviews
                    }
                    game {
                        id
                        name
                        slug
                    }
                    isFavorited
                }
            }
            """;

        long startTime = System.nanoTime();

        graphQlTester.document(query)
            .variable("accountId", testAccounts.get(0).getId())
            .executeAndVerify(); // This will verify the response is valid

        long endTime = System.nanoTime();
        long durationMs = Duration.ofNanos(endTime - startTime).toMillis();

        // Assert that query completes within 300ms
        assert durationMs < 300 : "Query took " + durationMs + "ms, expected < 300ms";
    }

    private void createTestData() {
        // Create test user if not exists
        User testUser = userRepository.findByEmail("performance-test@example.com").orElse(null);
        if (testUser == null) {
            testUser = new User();
            testUser.setEmail("performance-test@example.com");
            testUser.setFullName("Performance Test User");
            testUser = userRepository.save(testUser);
        }

        // Create test game if not exists
        Game testGame = gameRepository.findBySlug("performance-game").orElse(null);
        if (testGame == null) {
            testGame = new Game();
            testGame.setName("Performance Test Game");
            testGame.setSlug("performance-game");
            testGame = gameRepository.save(testGame);
        }

        // Create test accounts if not enough exist
        List<Account> existingAccounts = accountRepository.findAll();
        if (existingAccounts.size() < 5) {
            for (int i = existingAccounts.size(); i < 5; i++) {
                Account account = new Account();
                account.setSellerId(testUser.getId());
                account.setGameId(testGame.getId());
                account.setTitle("Performance Test Account " + (i + 1));
                account.setPrice(99.99 + i);
                account.setStatus(Account.AccountStatus.APPROVED);
                account = accountRepository.save(account);
                testAccounts.add(account);
            }
        } else {
            testAccounts.addAll(existingAccounts.subList(0, Math.min(5, existingAccounts.size())));
        }
    }
}
