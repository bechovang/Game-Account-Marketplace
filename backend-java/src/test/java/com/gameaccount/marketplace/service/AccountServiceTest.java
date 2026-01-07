package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.request.CreateAccountRequest;
import com.gameaccount.marketplace.dto.request.UpdateAccountRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.repository.GameRepository;
import com.gameaccount.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountService
 * Tests all business logic methods with various scenarios
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User testSeller;
    private Game testGame;
    private Account testAccount;
    private CreateAccountRequest createRequest;
    private UpdateAccountRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testSeller = User.builder()
                .id(1L)
                .email("seller@test.com")
                .fullName("Test Seller")
                .build();

        testGame = Game.builder()
                .id(1L)
                .name("Test Game")
                .slug("test-game")
                .build();

        testAccount = Account.builder()
                .id(1L)
                .seller(testSeller)
                .game(testGame)
                .title("Test Account")
                .description("Test Description")
                .level(50)
                .rank("Diamond")
                .price(100.0)
                .status(AccountStatus.PENDING)
                .viewsCount(0)
                .isFeatured(false)
                .images(Arrays.asList("http://image1.jpg"))
                .build();

        createRequest = new CreateAccountRequest();
        createRequest.setGameId(1L);
        createRequest.setTitle("Test Account");
        createRequest.setDescription("Test Description");
        createRequest.setLevel(50);
        createRequest.setRank("Diamond");
        createRequest.setPrice(100.0);
        createRequest.setImages(Arrays.asList("http://image1.jpg"));

        updateRequest = new UpdateAccountRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setLevel(60);
        updateRequest.setRank("Master");
        updateRequest.setPrice(150.0);
        updateRequest.setImages(Arrays.asList("http://image2.jpg"));
    }

    // ==================== createAccount() Tests ====================

    @Test
    void createAccount_ValidData_ReturnsAccount() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testSeller));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return account;
        });

        // When
        Account result = accountService.createAccount(createRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(result.getSeller()).isEqualTo(testSeller);
        assertThat(result.getGame()).isEqualTo(testGame);
        assertThat(result.getTitle()).isEqualTo("Test Account");
        assertThat(result.getPrice()).isEqualTo(100.0);
        assertThat(result.getViewsCount()).isEqualTo(0);
        assertThat(result.isFeatured()).isFalse();

        verify(userRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_NonExistentSeller_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> accountService.createAccount(createRequest, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Seller not found");

        verify(userRepository).findById(1L);
        verify(gameRepository, never()).findById(anyLong());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_NonExistentGame_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testSeller));
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> accountService.createAccount(createRequest, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Game not found");

        verify(userRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ==================== updateAccount() Tests ====================

    @Test
    void updateAccount_ByOwner_Success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Account result = accountService.updateAccount(1L, updateRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getLevel()).isEqualTo(60);
        assertThat(result.getRank()).isEqualTo("Master");
        assertThat(result.getPrice()).isEqualTo(150.0);

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void updateAccount_NotOwner_ThrowsException() {
        // Given
        Long differentUserId = 999L;
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When/Then
        assertThatThrownBy(() -> accountService.updateAccount(1L, updateRequest, differentUserId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not authorized");

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_NonExistentAccount_ThrowsException() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> accountService.updateAccount(1L, updateRequest, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ==================== deleteAccount() Tests ====================

    @Test
    void deleteAccount_ByOwner_Success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        doNothing().when(accountRepository).delete(any(Account.class));

        // When
        accountService.deleteAccount(1L, 1L, false);

        // Then
        verify(accountRepository).findById(1L);
        verify(accountRepository).delete(testAccount);
    }

    @Test
    void deleteAccount_ByAdmin_Success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        doNothing().when(accountRepository).delete(any(Account.class));

        // When
        accountService.deleteAccount(1L, 999L, true); // Different user but admin

        // Then
        verify(accountRepository).findById(1L);
        verify(accountRepository).delete(testAccount);
    }

    @Test
    void deleteAccount_NotOwnerAndNotAdmin_ThrowsException() {
        // Given
        Long differentUserId = 999L;
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When/Then
        assertThatThrownBy(() -> accountService.deleteAccount(1L, differentUserId, false))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not authorized");

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    void deleteAccount_NonExistentAccount_ThrowsException() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> accountService.deleteAccount(1L, 1L, false))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).delete(any(Account.class));
    }

    // ==================== getAccountById() Tests ====================

    @Test
    void getAccountById_ValidAccount_IncrementsViewsCount() {
        // Given
        testAccount.setViewsCount(100);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Account result = accountService.getAccountById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getViewsCount()).isEqualTo(101);

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void getAccountById_NonExistentAccount_ThrowsException() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> accountService.getAccountById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ==================== approveAccount() Tests ====================

    @Test
    void approveAccount_ValidPendingAccount_ChangesStatus() {
        // Given
        testAccount.setStatus(AccountStatus.PENDING);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Account result = accountService.approveAccount(1L);

        // Then
        assertThat(result.getStatus()).isEqualTo(AccountStatus.APPROVED);

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void approveAccount_AlreadyApproved_ThrowsException() {
        // Given
        testAccount.setStatus(AccountStatus.APPROVED);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When/Then
        assertThatThrownBy(() -> accountService.approveAccount(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only pending accounts can be approved");

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void approveAccount_NonExistentAccount_ThrowsException() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> accountService.approveAccount(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ==================== rejectAccount() Tests ====================

    @Test
    void rejectAccount_ValidPendingAccount_ChangesStatus() {
        // Given
        testAccount.setStatus(AccountStatus.PENDING);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Account result = accountService.rejectAccount(1L, "Invalid price");

        // Then
        assertThat(result.getStatus()).isEqualTo(AccountStatus.REJECTED);

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void rejectAccount_AlreadyRejected_ThrowsException() {
        // Given
        testAccount.setStatus(AccountStatus.REJECTED);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When/Then
        assertThatThrownBy(() -> accountService.rejectAccount(1L, "Invalid price"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only pending accounts can be rejected");

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ==================== searchAccounts() Tests ====================

    @Test
    void searchAccounts_WithFilters_ReturnsPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount), pageable, 1);

        when(accountRepository.searchAccounts(1L, 50.0, 200.0, AccountStatus.APPROVED, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(1L, 50.0, 200.0, AccountStatus.APPROVED, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testAccount);

        verify(accountRepository).searchAccounts(1L, 50.0, 200.0, AccountStatus.APPROVED, pageable);
    }

    @Test
    void searchAccounts_NoFilters_ReturnsPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount), pageable, 1);

        when(accountRepository.searchAccounts(null, null, null, null, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(null, null, null, null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(accountRepository).searchAccounts(null, null, null, null, pageable);
    }

    @Test
    void searchAccounts_EmptyResults_ReturnsEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(accountRepository.searchAccounts(999L, null, null, null, pageable))
                .thenReturn(emptyPage);

        // When
        Page<Account> result = accountService.searchAccounts(999L, null, null, null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(accountRepository).searchAccounts(999L, null, null, null, pageable);
    }

    @Test
    void searchAccounts_WithStatusFilter_ReturnsFilteredPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Account approvedAccount = Account.builder()
                .id(2L)
                .status(AccountStatus.APPROVED)
                .build();
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(approvedAccount), pageable, 1);

        when(accountRepository.searchAccounts(null, null, null, AccountStatus.APPROVED, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(null, null, null, AccountStatus.APPROVED, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(AccountStatus.APPROVED);

        verify(accountRepository).searchAccounts(null, null, null, AccountStatus.APPROVED, pageable);
    }

    @Test
    void searchAccounts_WithPriceFilter_ReturnsFilteredPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Account expensiveAccount = Account.builder()
                .id(2L)
                .price(500.0)
                .build();
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(expensiveAccount), pageable, 1);

        when(accountRepository.searchAccounts(null, 100.0, 1000.0, null, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Account> result = accountService.searchAccounts(null, 100.0, 1000.0, null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPrice()).isEqualTo(500.0);

        verify(accountRepository).searchAccounts(null, 100.0, 1000.0, null, pageable);
    }

    @Test
    void searchAccounts_CachingBehavior() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> expectedPage = new PageImpl<>(Arrays.asList(testAccount), pageable, 1);

        when(accountRepository.searchAccounts(1L, 50.0, 200.0, AccountStatus.APPROVED, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Account> result1 = accountService.searchAccounts(1L, 50.0, 200.0, AccountStatus.APPROVED, pageable);
        Page<Account> result2 = accountService.searchAccounts(1L, 50.0, 200.0, AccountStatus.APPROVED, pageable);

        // Then
        assertThat(result1).isEqualTo(result2);
        // Note: In unit tests, @Cacheable annotation is not proxied, so repository is called twice
        // Caching would be verified in integration tests with @SpringBootTest
        verify(accountRepository, times(2)).searchAccounts(1L, 50.0, 200.0, AccountStatus.APPROVED, pageable);
    }
}
