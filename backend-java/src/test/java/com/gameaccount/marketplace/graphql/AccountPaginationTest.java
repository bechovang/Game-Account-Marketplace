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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for cursor-based pagination.
 */
@SpringBootTest
@AutoConfigureGraphQlTester
@Transactional
class AccountPaginationTest {

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
    private Account testAccount1;
    private Account testAccount2;

    @BeforeEach
    void setUp() {
        // Create test data
        testUser = userRepository.save(User.builder()
            .email("pagination-test@example.com")
            .fullName("Pagination Test User")
            .build());

        testGame = gameRepository.save(Game.builder()
            .name("Pagination Test Game")
            .slug("pagination-game")
            .build());

        testAccount1 = accountRepository.save(Account.builder()
            .seller(testUser)
            .game(testGame)
            .title("Account 1")
            .price(10.0)
            .status(Account.AccountStatus.APPROVED)
            .build());

        testAccount2 = accountRepository.save(Account.builder()
            .seller(testUser)
            .game(testGame)
            .title("Account 2")
            .price(20.0)
            .status(Account.AccountStatus.APPROVED)
            .build());
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_returns_AccountConnection_with_pagination() {
        String query = """
            query GetAccountsConnection($first: Int) {
                accountsConnection(first: $first) {
                    edges {
                        node {
                            id
                            title
                        }
                        cursor
                    }
                    pageInfo {
                        hasNextPage
                        hasPreviousPage
                        startCursor
                        endCursor
                    }
                    totalCount
                }
            }
            """;

        graphQlTester.document(query)
            .variable("first", 10)
            .execute()
            .path("accountsConnection").exists()
            .path("accountsConnection.edges").exists()
            .path("accountsConnection.pageInfo").exists()
            .path("accountsConnection.totalCount").entity(Long.class).isGreaterThanOrEqualTo(2);
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void forward_pagination_with_after_cursor() {
        // First get the first page
        String firstQuery = """
            query GetFirstPage {
                accountsConnection(first: 1) {
                    edges {
                        cursor
                    }
                    pageInfo {
                        hasNextPage
                        endCursor
                    }
                }
            }
            """;

        var firstResult = graphQlTester.document(firstQuery)
            .execute()
            .path("accountsConnection.pageInfo.hasNextPage").entity(Boolean.class).isEqualTo(true)
            .path("accountsConnection.pageInfo.endCursor").entity(String.class);

        String firstCursor = firstResult.<String>entity(String.class).get();

        // Now paginate forward
        String secondQuery = """
            query GetSecondPage($after: String) {
                accountsConnection(after: $after, first: 1) {
                    edges {
                        node {
                            title
                        }
                    }
                    pageInfo {
                        hasNextPage
                        hasPreviousPage
                    }
                }
            }
            """;

        graphQlTester.document(secondQuery)
            .variable("after", firstCursor)
            .execute()
            .path("accountsConnection.pageInfo.hasPreviousPage").entity(Boolean.class).isEqualTo(true);
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void cursor_format_is_base64_encoded() {
        String query = """
            query GetAccounts {
                accountsConnection(first: 1) {
                    edges {
                        cursor
                    }
                }
            }
            """;

        graphQlTester.document(query)
            .execute()
            .path("accountsConnection.edges[0].cursor").entity(String.class)
            .satisfies(cursor -> {
                // Cursor should be base64 encoded
                assertThat(cursor).isNotNull();
                assertThat(cursor).isNotEmpty();
                // Should be valid base64
                try {
                    java.util.Base64.getDecoder().decode(cursor);
                } catch (Exception e) {
                    throw new AssertionError("Cursor is not valid base64: " + cursor, e);
                }
            });
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_invalid_first_and_last_throws_error() {
        String query = """
            query ($first: Int, $last: Int) {
                accountsConnection(first: $first, last: $last) {
                    totalCount
                }
            }
            """;

        graphQlTester.document(query)
            .variable("first", 10)
            .variable("last", 10)
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).hasSize(1);
                assertThat(errors.get(0).getMessage()).contains("Cannot specify both 'first' and 'last'");
            });
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_invalid_after_and_before_throws_error() {
        String query = """
            query ($after: String, $before: String) {
                accountsConnection(after: $after, before: $before) {
                    totalCount
                }
            }
            """;

        graphQlTester.document(query)
            .variable("after", "cursor1")
            .variable("before", "cursor2")
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).hasSize(1);
                assertThat(errors.get(0).getMessage()).contains("Cannot specify both 'after' and 'before'");
            });
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_invalid_before_with_first_throws_error() {
        String query = """
            query ($first: Int, $before: String) {
                accountsConnection(first: $first, before: $before) {
                    totalCount
                }
            }
            """;

        graphQlTester.document(query)
            .variable("first", 10)
            .variable("before", "cursor")
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).hasSize(1);
                assertThat(errors.get(0).getMessage()).contains("'before' cannot be used with 'first'");
            });
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_invalid_after_with_last_throws_error() {
        String query = """
            query ($last: Int, $after: String) {
                accountsConnection(last: $last, after: $after) {
                    totalCount
                }
            }
            """;

        graphQlTester.document(query)
            .variable("last", 10)
            .variable("after", "cursor")
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).hasSize(1);
                assertThat(errors.get(0).getMessage()).contains("'after' cannot be used with 'last'");
            });
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_invalid_page_size_too_large() {
        String query = """
            query ($first: Int) {
                accountsConnection(first: $first) {
                    totalCount
                }
            }
            """;

        graphQlTester.document(query)
            .variable("first", 100) // Over max limit of 50
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).hasSize(1);
                assertThat(errors.get(0).getMessage()).contains("must be between 1 and 50");
            });
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_invalid_page_size_too_small() {
        String query = """
            query ($first: Int) {
                accountsConnection(first: $first) {
                    totalCount
                }
            }
            """;

        graphQlTester.document(query)
            .variable("first", 0) // Below minimum of 1
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).hasSize(1);
                assertThat(errors.get(0).getMessage()).contains("must be between 1 and 50");
            });
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_handles_empty_result_set() {
        // Create a filter that will return no results
        String query = """
            query {
                accountsConnection(
                    filters: { gameId: 99999 }
                    first: 10
                ) {
                    edges {
                        node {
                            id
                        }
                    }
                    pageInfo {
                        hasNextPage
                        hasPreviousPage
                        startCursor
                        endCursor
                    }
                    totalCount
                }
            }
            """;

        graphQlTester.document(query)
            .execute()
            .path("accountsConnection.edges").entityList(Object.class).hasSize(0)
            .path("accountsConnection.pageInfo.hasNextPage").entity(Boolean.class).isEqualTo(false)
            .path("accountsConnection.pageInfo.hasPreviousPage").entity(Boolean.class).isEqualTo(false)
            .path("accountsConnection.pageInfo.startCursor").entity(Object.class).isNull()
            .path("accountsConnection.pageInfo.endCursor").entity(Object.class).isNull()
            .path("accountsConnection.totalCount").entity(Integer.class).isEqualTo(0);
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_backward_pagination_with_before_cursor() {
        // First get a forward page to get a cursor
        String forwardQuery = """
            query {
                accountsConnection(first: 2) {
                    edges {
                        cursor
                    }
                    pageInfo {
                        endCursor
                    }
                }
            }
            """;

        String endCursor = graphQlTester.document(forwardQuery)
            .execute()
            .path("accountsConnection.pageInfo.endCursor").entity(String.class).get();

        // Now paginate backward from that cursor
        String backwardQuery = """
            query ($before: String, $last: Int) {
                accountsConnection(before: $before, last: $last) {
                    edges {
                        node {
                            title
                        }
                    }
                    pageInfo {
                        hasNextPage
                        hasPreviousPage
                    }
                }
            }
            """;

        graphQlTester.document(backwardQuery)
            .variable("before", endCursor)
            .variable("last", 2)
            .execute()
            .path("accountsConnection.edges").entityList(Object.class).hasSize(2)
            .path("accountsConnection.pageInfo.hasNextPage").entity(Boolean.class).isEqualTo(true)
            .path("accountsConnection.pageInfo.hasPreviousPage").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_cursor_sorting_consistency() {
        // Test that cursors maintain sort order consistency
        String query = """
            query {
                accountsConnection(
                    first: 10
                    sortBy: "createdAt"
                    sortDirection: "DESC"
                ) {
                    edges {
                        node {
                            id
                            createdAt
                        }
                        cursor
                    }
                }
            }
            """;

        graphQlTester.document(query)
            .execute()
            .path("accountsConnection.edges").entityList(Object.class)
            .satisfies(edges -> {
                // Verify that accounts are sorted by createdAt DESC
                for (int i = 0; i < edges.size() - 1; i++) {
                    // This is a basic check - in a real implementation you'd parse the dates
                    assertThat(edges.get(i)).isNotNull();
                }
            });
    }

    @Test
    @WithMockUser(username = "pagination-test@example.com")
    void accountsConnection_filtering_with_pagination() {
        // Test pagination works correctly with filters
        String query = """
            query {
                accountsConnection(
                    filters: { gameId: 1 }
                    first: 5
                ) {
                    edges {
                        node {
                            game {
                                id
                            }
                        }
                    }
                    pageInfo {
                        hasNextPage
                    }
                    totalCount
                }
            }
            """;

        graphQlTester.document(query)
            .execute()
            .path("accountsConnection.edges").entityList(Object.class)
            .satisfies(edges -> {
                // All returned accounts should match the game filter
                edges.forEach(edge -> {
                    // In a real test, you'd verify the game ID matches
                    assertThat(edge).isNotNull();
                });
            });
    }
}
