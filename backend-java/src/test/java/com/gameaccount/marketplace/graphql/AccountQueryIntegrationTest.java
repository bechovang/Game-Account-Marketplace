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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for GraphQL queries with DataLoader N+1 prevention.
 */
@SpringBootTest
@AutoConfigureGraphQlTester
@Transactional
class AccountQueryIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    private User testUser;
    private Game testGame;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        // Create test data
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test Seller");
        testUser = userRepository.save(testUser);

        testGame = new Game();
        testGame.setName("Test Game");
        testGame.setSlug("test-game");
        testGame = gameRepository.save(testGame);

        testAccount = new Account();
        testAccount.setSellerId(testUser.getId());
        testAccount.setGameId(testGame.getId());
        testAccount.setTitle("Test Account");
        testAccount.setPrice(99.99);
        testAccount.setStatus(Account.AccountStatus.APPROVED);
        testAccount = accountRepository.save(testAccount);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void accountQuery_returnsAccountWithSellerAndGame() {
        String query = """
            query GetAccount($accountId: ID!) {
                account(id: $accountId) {
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
                }
            }
            """;

        graphQlTester.document(query)
            .variable("accountId", testAccount.getId())
            .execute()
            .path("account.id").entity(Long.class).isEqualTo(testAccount.getId())
            .path("account.title").entity(String.class).isEqualTo("Test Account")
            .path("account.seller.id").entity(Long.class).isEqualTo(testUser.getId())
            .path("account.seller.fullName").entity(String.class).isEqualTo("Test Seller")
            .path("account.game.id").entity(Long.class).isEqualTo(testGame.getId())
            .path("account.game.name").entity(String.class).isEqualTo("Test Game");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void accountsQuery_returnsPaginatedResultsWithSellerAndGame() {
        String query = """
            query GetAccounts {
                accounts(page: 0, limit: 10) {
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
                    }
                    totalElements
                    currentPage
                }
            }
            """;

        graphQlTester.document(query)
            .execute()
            .path("accounts.content").entityList(Account.class).hasSizeGreaterThan(0)
            .path("accounts.totalElements").entity(Long.class).isEqualTo(1L)
            .path("accounts.currentPage").entity(Integer.class).isEqualTo(0);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void accountQuery_withIsFavoritedField() {
        String query = """
            query GetAccount($accountId: ID!) {
                account(id: $accountId) {
                    id
                    isFavorited
                    seller {
                        id
                        fullName
                    }
                    game {
                        id
                        name
                    }
                }
            }
            """;

        graphQlTester.document(query)
            .variable("accountId", testAccount.getId())
            .execute()
            .path("account.id").entity(Long.class).isEqualTo(testAccount.getId())
            .path("account.isFavorited").entity(Boolean.class);
    }
}
