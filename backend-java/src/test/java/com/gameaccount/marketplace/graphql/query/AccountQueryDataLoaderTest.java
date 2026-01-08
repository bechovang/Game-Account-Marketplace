package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.graphql.dto.PaginatedAccountResponse;
import com.gameaccount.marketplace.service.AccountService;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AccountQuery DataLoader integration.
 */
class AccountQueryDataLoaderTest {

    @Mock
    private AccountService accountService;

    private AccountQuery accountQuery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountQuery = new AccountQuery(accountService);
    }

    @Test
    void seller_field_fallbacks_when_no_DataLoader() {
        // Given
        Account account = createTestAccount(1L, 100L, 200L);
        User expectedUser = createTestUser(100L);
        account.setSeller(expectedUser);

        // Mock GraphQL context with no DataLoaderRegistry
        graphql.GraphQLContext context = new graphql.GraphQLContext();

        // When
        CompletableFuture<User> result = accountQuery.seller(account, context);

        // Then - should fallback to direct access
        assertThat(result).isCompletedWithValue(expectedUser);
    }

    @Test
    void game_field_fallbacks_when_no_DataLoader() {
        // Given
        Account account = createTestAccount(1L, 100L, 200L);
        Game expectedGame = createTestGame(200L);
        account.setGame(expectedGame);

        // Mock GraphQL context with no DataLoaderRegistry
        graphql.GraphQLContext context = new graphql.GraphQLContext();

        // When
        CompletableFuture<Game> result = accountQuery.game(account, context);

        // Then - should fallback to direct access
        assertThat(result).isCompletedWithValue(expectedGame);
    }

    @Test
    void seller_field_usesDataLoader_when_available() {
        // Given
        Account account = createTestAccount(1L, 100L, 200L);
        User expectedUser = createTestUser(100L);

        // Create mock DataLoaderRegistry and loaders
        DataLoaderRegistry registry = mock(DataLoaderRegistry.class);
        DataLoader<Long, User> userLoader = mock(DataLoader.class);

        when(registry.getDataLoader("userLoader")).thenReturn(userLoader);
        when(userLoader.load(100L)).thenReturn(CompletableFuture.completedFuture(expectedUser));

        // Mock GraphQL context with DataLoaderRegistry
        graphql.GraphQLContext context = new graphql.GraphQLContext();
        context.put("dataLoaderRegistry", registry);

        // When
        CompletableFuture<User> result = accountQuery.seller(account, context);

        // Then
        assertThat(result).isCompletedWithValue(expectedUser);
    }

    @Test
    void accounts_query_delegatesToService() {
        // Given
        List<Account> accounts = List.of(createTestAccount(1L, 100L, 200L));
        Page<Account> page = new PageImpl<>(accounts);
        when(accountService.searchAccounts(any(), any(), any(), any())).thenReturn(page);

        // When
        PaginatedAccountResponse result = accountQuery.accounts(null, null, null, null, null, null, null, 0, 10);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    private Account createTestAccount(Long accountId, Long sellerId, Long gameId) {
        Account account = new Account();
        account.setId(accountId);
        User seller = createTestUser(sellerId);
        Game game = createTestGame(gameId);
        account.setSeller(seller);
        account.setGame(game);
        account.setTitle("Test Account");
        return account;
    }

    private User createTestUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setFullName("Test User");
        return user;
    }

    private Game createTestGame(Long gameId) {
        Game game = new Game();
        game.setId(gameId);
        game.setName("Test Game");
        return game;
    }
}
