package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.dto.request.AccountSearchRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.graphql.dto.PaginatedAccountResponse;
import com.gameaccount.marketplace.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AccountQuery GraphQL resolver.
 * Tests delegation to AccountService and response wrapping.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountQueryTest {

    @Mock
    private AccountService accountService;

    @Mock
    private com.gameaccount.marketplace.util.CursorUtil cursorUtil;

    @Mock
    private com.gameaccount.marketplace.service.PaginationService paginationService;

    @InjectMocks
    private AccountQuery accountQuery;

    private Account testAccount;
    private Page<Account> testPage;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .title("Test Account")
                .price(100.0)
                .status(AccountStatus.APPROVED)
                .build();

        testPage = new PageImpl<>(Arrays.asList(testAccount), PageRequest.of(0, 20), 1);
    }

    // ==================== accounts() query tests ====================

    @Test
    void accounts_WithDefaults_ReturnsPaginatedResponse() {
        // Given - Use lenient stubbing with any() for nullable parameters
        when(accountService.searchAccounts(any(), any(), any(), any()))
                .thenReturn(testPage);
        when(accountService.getAllowedSortFields()).thenReturn(java.util.Set.of("price", "level", "createdAt"));

        // When
        PaginatedAccountResponse response = accountQuery.accounts(null, null, null, null, null, null, null, null, null);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("Test Account");
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(20);
        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getTotalPages()).isEqualTo(1);
    }

    @Test
    void accounts_WithFilters_ReturnsFilteredResults() {
        // Given
        Long gameId = 1L;
        Double minPrice = 50.0;
        Double maxPrice = 200.0;
        String status = "APPROVED";
        Integer page = 0;
        Integer limit = 10;

        when(accountService.searchAccounts(any(), any(), any(), any()))
                .thenReturn(testPage);
        when(accountService.getAllowedSortFields()).thenReturn(java.util.Set.of("price", "level", "createdAt"));

        // When
        PaginatedAccountResponse response = accountQuery.accounts(gameId, minPrice, maxPrice, status, null, null, null, page, limit);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void accounts_WithInvalidStatus_IgnoresStatusFilter() {
        // Given
        when(accountService.searchAccounts(any(), any(), any(), any()))
                .thenReturn(testPage);
        when(accountService.getAllowedSortFields()).thenReturn(java.util.Set.of("price", "level", "createdAt"));

        // When
        PaginatedAccountResponse response = accountQuery.accounts(null, null, null, "INVALID", null, null, null, null, null);

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    void accounts_WithCustomPagination_UsesCustomValues() {
        // Given
        when(accountService.searchAccounts(any(), any(), any(), any()))
                .thenReturn(testPage);
        when(accountService.getAllowedSortFields()).thenReturn(java.util.Set.of("price", "level", "createdAt"));

        // When
        PaginatedAccountResponse response = accountQuery.accounts(null, null, null, null, null, null, null, 2, 50);

        // Then
        assertThat(response.getCurrentPage()).isEqualTo(2);
        assertThat(response.getPageSize()).isEqualTo(50);
    }

    @Test
    void accounts_WithLimitOver100_CapsAt100() {
        // Given
        when(accountService.searchAccounts(any(), any(), any(), any()))
                .thenReturn(testPage);
        when(accountService.getAllowedSortFields()).thenReturn(java.util.Set.of("price", "level", "createdAt"));

        // When
        PaginatedAccountResponse response = accountQuery.accounts(null, null, null, null, null, null, null, null, 150);

        // Then
        assertThat(response.getPageSize()).isEqualTo(100);
    }

    @Test
    void accounts_EmptyResults_ReturnsEmptyPage() {
        // Given
        Page<Account> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0);
        when(accountService.searchAccounts(any(), any(), any(), any()))
                .thenReturn(emptyPage);
        when(accountService.getAllowedSortFields()).thenReturn(java.util.Set.of("price", "level", "createdAt"));

        // When
        PaginatedAccountResponse response = accountQuery.accounts(null, null, null, null, null, null, null, null, null);

        // Then
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0L);
    }

    // ==================== account() query tests ====================

    @Test
    void account_WithValidId_ReturnsAccount() {
        // Given
        Long accountId = 1L;
        when(accountService.getAccountByIdWithoutIncrement(accountId)).thenReturn(testAccount);

        // When
        Account result = accountQuery.account(accountId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Account");

        verify(accountService).getAccountByIdWithoutIncrement(accountId);
    }

    @Test
    void account_WithNullId_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> accountQuery.account(null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Valid account ID is required");
    }

    @Test
    void account_WithNegativeId_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> accountQuery.account(-1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Valid account ID is required");
    }

    @Test
    void account_WithZeroId_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> accountQuery.account(0L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Valid account ID is required");
    }
}
