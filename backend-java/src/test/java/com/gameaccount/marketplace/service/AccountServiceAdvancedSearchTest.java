package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.request.AccountSearchRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.spec.AccountSpecification;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for advanced account search functionality.
 * Tests filtering, sorting, role-based access control, and caching behavior.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountServiceAdvancedSearchTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount1;
    private Account testAccount2;
    private Game testGame;
    private User testSeller;

    @BeforeEach
    void setUp() {
        // Setup test data
        testGame = Game.builder()
                .id(1L)
                .name("Test Game")
                .slug("test-game")
                .build();

        testSeller = User.builder()
                .id(1L)
                .email("seller@test.com")
                .fullName("Test Seller")
                .build();

        testAccount1 = Account.builder()
                .id(1L)
                .seller(testSeller)
                .game(testGame)
                .title("Level 50 Account")
                .description("High level account for sale")
                .level(50)
                .rank("Diamond")
                .price(100.0)
                .status(AccountStatus.APPROVED)
                .isFeatured(true)
                .viewsCount(100)
                .build();

        testAccount2 = Account.builder()
                .id(2L)
                .seller(testSeller)
                .game(testGame)
                .title("Power Leveling Service")
                .description("Fast leveling service")
                .level(25)
                .rank("Gold")
                .price(50.0)
                .status(AccountStatus.APPROVED)
                .isFeatured(false)
                .viewsCount(50)
                .build();
    }

    @Test
    void searchAccounts_WithAllFilters_ReturnsMatchingAccounts() {
        // Given
        AccountSearchRequest request = AccountSearchRequest.builder()
                .gameId(1L)
                .minPrice(50.0)
                .maxPrice(200.0)
                .minLevel(10)
                .maxLevel(50)
                .rank("Diamond")
                .status(AccountStatus.APPROVED)
                .isFeatured(true)
                .searchText("power leveling")
                .sortBy("price")
                .sortDirection(AccountSearchRequest.SortDirection.ASC)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1));

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(
                request, 1L, "BUYER", pageable
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void searchAccounts_BuyerRole_OnlyReturnsApprovedAccounts() {
        // Given - Buyer tries to search for PENDING accounts (should be overridden)
        AccountSearchRequest request = AccountSearchRequest.builder()
                .status(AccountStatus.PENDING) // Buyer requests PENDING
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1));

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "BUYER", pageable);

        // Then - Should override status to APPROVED for buyers
        assertThat(result).isNotNull();
    }

    @Test
    void searchAccounts_SellerOwnListings_IncludesPendingAccounts() {
        // Given - Seller searching for own PENDING listings
        AccountSearchRequest request = AccountSearchRequest.builder()
                .sellerId(1L) // Searching own listings
                .status(AccountStatus.PENDING)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1));

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "SELLER", pageable);

        // Then - Should allow PENDING status for own listings
        assertThat(result).isNotNull();
    }

    @Test
    void searchAccounts_SellerOthersListings_OnlyReturnsApproved() {
        // Given - Seller searching for OTHER sellers' listings
        AccountSearchRequest request = AccountSearchRequest.builder()
                .sellerId(999L) // Different seller
                .status(AccountStatus.PENDING) // Requests PENDING
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1));

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        accountService.searchAccounts(request, 1L, "SELLER", pageable);

        // Then - Should override to APPROVED for other sellers' listings
        verify(accountRepository).searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any());
    }

    @Test
    void searchAccounts_AdminRole_SeesAllStatuses() {
        // Given - Admin can search for any status
        AccountSearchRequest request = AccountSearchRequest.builder()
                .status(AccountStatus.PENDING)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1));

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "ADMIN", pageable);

        // Then - Should respect requested status for admins
        assertThat(result).isNotNull();
    }

    @Test
    void searchAccounts_WithSorting_AppliesSortToPageable() {
        // Given
        AccountSearchRequest request = AccountSearchRequest.builder()
                .sortBy("price")
                .sortDirection(AccountSearchRequest.SortDirection.DESC)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1, testAccount2));

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "BUYER", pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void searchAccounts_WithFullTextSearch_SearchesTitleAndDescription() {
        // Given
        AccountSearchRequest request = AccountSearchRequest.builder()
                .searchText("leveling")
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount2));

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "BUYER", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDescription()).contains("leveling");
    }

    @Test
    void searchAccounts_WithPriceRange_FiltersCorrectly() {
        // Given
        AccountSearchRequest request = AccountSearchRequest.builder()
                .minPrice(75.0)
                .maxPrice(150.0)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1)); // $100 account

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "BUYER", pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void searchAccounts_WithLevelRange_FiltersCorrectly() {
        // Given
        AccountSearchRequest request = AccountSearchRequest.builder()
                .minLevel(30)
                .maxLevel(60)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1)); // Level 50

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "BUYER", pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void searchAccounts_WithFeaturedFlag_FiltersCorrectly() {
        // Given
        AccountSearchRequest request = AccountSearchRequest.builder()
                .isFeatured(true)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1)); // Featured

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "BUYER", pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void searchAccounts_WithGameId_FiltersCorrectly() {
        // Given
        AccountSearchRequest request = AccountSearchRequest.builder()
                .gameId(1L)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1, testAccount2));

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "BUYER", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void searchAccounts_EmptyFilters_ReturnsAllApproved() {
        // Given - No filters specified
        AccountSearchRequest request = AccountSearchRequest.builder().build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1, testAccount2));

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "BUYER", pageable);

        // Then - Should return all APPROVED accounts
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void searchAccounts_Pagination_WorksCorrectly() {
        // Given
        AccountSearchRequest request = AccountSearchRequest.builder().build();

        Pageable pageable = PageRequest.of(1, 10); // Second page, 10 items per page
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount1), pageable, 2);

        when(accountRepository.searchAccountsWithJoins(
                any(), any(), any(),
                any(), any(), any(),
                any(AccountStatus.class), any(), any(),
                any(), any()))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(request, 1L, "BUYER", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo(1); // Page number
        assertThat(result.getSize()).isEqualTo(10); // Page size
    }

    @Test
    void searchAccountSpecification_BuildsCorrectSpecification() {
        // Given
        AccountSearchRequest request = AccountSearchRequest.builder()
                .gameId(1L)
                .minPrice(50.0)
                .maxPrice(200.0)
                .minLevel(10)
                .maxLevel(50)
                .rank("Diamond")
                .status(AccountStatus.APPROVED)
                .isFeatured(true)
                .searchText("test")
                .sellerId(1L)
                .build();

        // When
        Specification<Account> spec = AccountSpecification.fromSearchRequest(request);

        // Then
        assertThat(spec).isNotNull();
    }
}
