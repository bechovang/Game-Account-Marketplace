package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.graphql.dto.PaginatedAccountResponse;
import com.gameaccount.marketplace.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AccountQuery field resolvers.
 */
class AccountQueryDataLoaderTest {

    @Mock
    private AccountService accountService;

    @Mock
    private com.gameaccount.marketplace.util.CursorUtil cursorUtil;

    @Mock
    private com.gameaccount.marketplace.service.PaginationService paginationService;

    private AccountQuery accountQuery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountQuery = new AccountQuery(accountService, cursorUtil, paginationService);
    }

    @Test
    void seller_field_returns_seller_from_account() {
        // Given
        Account account = createTestAccount(1L, 100L, 200L);
        User expectedUser = createTestUser(100L);
        account.setSeller(expectedUser);

        // When
        User result = accountQuery.seller(account);

        // Then - should return seller from account
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedUser.getId());
        assertThat(result.getFullName()).isEqualTo(expectedUser.getFullName());
    }

    @Test
    void game_field_returns_game_from_account() {
        // Given
        Account account = createTestAccount(1L, 100L, 200L);
        Game expectedGame = createTestGame(200L);
        account.setGame(expectedGame);

        // When
        Game result = accountQuery.game(account);

        // Then - should return game from account
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedGame.getId());
        assertThat(result.getName()).isEqualTo(expectedGame.getName());
    }

    @Test
    void accounts_query_delegatesToService() {
        // Given
        List<Account> accounts = List.of(createTestAccount(1L, 100L, 200L));
        Page<Account> page = new PageImpl<>(accounts);
        when(accountService.searchAccounts(any(), any(), any(), any())).thenReturn(page);
        when(accountService.getAllowedSortFields()).thenReturn(java.util.Set.of("price", "level", "createdAt"));

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
