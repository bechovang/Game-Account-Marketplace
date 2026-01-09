package com.gameaccount.marketplace.graphql.mutation;

import com.gameaccount.marketplace.dto.request.CreateAccountRequest;
import com.gameaccount.marketplace.dto.request.UpdateAccountRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.graphql.dto.CreateAccountInput;
import com.gameaccount.marketplace.graphql.dto.UpdateAccountInput;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AccountMutation GraphQL resolver.
 * Tests delegation to AccountService and authentication/authorization.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountMutationTest {

    @Mock
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountMutation accountMutation;

    private CreateAccountInput createInput;
    private UpdateAccountInput updateInput;
    private Account testAccount;
    private Authentication userAuth;
    private Authentication adminAuth;

    @BeforeEach
    void setUp() {
        createInput = new CreateAccountInput();
        createInput.setGameId(1L);
        createInput.setTitle("Test Account");
        createInput.setDescription("Test Description");
        createInput.setLevel(50);
        createInput.setRank("Diamond");
        createInput.setPrice(100.0);
        createInput.setImages(Arrays.asList("http://image1.jpg"));

        updateInput = new UpdateAccountInput();
        updateInput.setTitle("Updated Title");
        updateInput.setDescription("Updated Description");
        updateInput.setLevel(60);
        updateInput.setRank("Master");
        updateInput.setPrice(150.0);
        updateInput.setImages(Arrays.asList("http://image2.jpg"));

        testAccount = Account.builder()
                .id(1L)
                .title("Test Account")
                .status(AccountStatus.PENDING)
                .build();

        // Setup user authentication (not admin) - UsernamePasswordAuthenticationToken is NOT anonymous
        userAuth = new UsernamePasswordAuthenticationToken(
                "123", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // Setup admin authentication
        adminAuth = new UsernamePasswordAuthenticationToken(
                "123", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // Mock UserRepository to return user with ID 123 when email "123" is queried
        User testUser = User.builder().id(123L).email("123").build();
        when(userRepository.findByEmail("123")).thenReturn(java.util.Optional.of(testUser));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== createAccount() tests ====================

    @Test
    void createAccount_WithAuthenticatedUser_CreatesAccount() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        when(accountService.createAccount(any(CreateAccountRequest.class), eq(123L)))
                .thenReturn(testAccount);

        // When
        Account result = accountMutation.createAccount(createInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(accountService).createAccount(any(CreateAccountRequest.class), eq(123L));
    }

    @Test
    void createAccount_WithUnauthenticatedUser_ThrowsException() {
        // Given - no authentication set

        // When/Then
        assertThatThrownBy(() -> accountMutation.createAccount(createInput))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User must be authenticated");
    }

    @Test
    void createAccount_WithInvalidUserId_ThrowsException() {
        // Given
        Authentication invalidAuth = new UsernamePasswordAuthenticationToken(
                "invalid", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(invalidAuth);
        when(userRepository.findByEmail("invalid")).thenReturn(java.util.Optional.empty());

        // When/Then
        assertThatThrownBy(() -> accountMutation.createAccount(createInput))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User not found: invalid");
    }

    // ==================== updateAccount() tests ====================

    @Test
    void updateAccount_WithValidInput_UpdatesAccount() {
        // Given
        Long accountId = 1L;
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        when(accountService.updateAccount(eq(accountId), any(UpdateAccountRequest.class), eq(123L)))
                .thenReturn(testAccount);

        // When
        Account result = accountMutation.updateAccount(accountId, updateInput);

        // Then
        assertThat(result).isNotNull();

        verify(accountService).updateAccount(eq(accountId), any(UpdateAccountRequest.class), eq(123L));
    }

    @Test
    void updateAccount_WithUnauthenticatedUser_ThrowsException() {
        // Given - no authentication set

        // When/Then
        assertThatThrownBy(() -> accountMutation.updateAccount(1L, updateInput))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User must be authenticated");
    }

    // ==================== deleteAccount() tests ====================

    @Test
    void deleteAccount_AsOwner_DeletesAccount() {
        // Given
        Long accountId = 1L;
        SecurityContextHolder.getContext().setAuthentication(userAuth);

        // When
        Boolean result = accountMutation.deleteAccount(accountId);

        // Then
        assertThat(result).isTrue();

        verify(accountService).deleteAccount(accountId, 123L, false);
    }

    @Test
    void deleteAccount_AsAdmin_DeletesAccount() {
        // Given
        Long accountId = 1L;
        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        // When
        Boolean result = accountMutation.deleteAccount(accountId);

        // Then
        assertThat(result).isTrue();

        verify(accountService).deleteAccount(accountId, 123L, true);
    }

    @Test
    void deleteAccount_WithUnauthenticatedUser_ThrowsException() {
        // Given - no authentication set

        // When/Then
        assertThatThrownBy(() -> accountMutation.deleteAccount(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User must be authenticated");
    }

    // ==================== approveAccount() tests ====================

    @Test
    void approveAccount_AsAdmin_ApprovesAccount() {
        // Given
        Long accountId = 1L;
        SecurityContextHolder.getContext().setAuthentication(adminAuth);
        testAccount.setStatus(AccountStatus.APPROVED);
        when(accountService.approveAccount(accountId)).thenReturn(testAccount);

        // When
        Account result = accountMutation.approveAccount(accountId);

        // Then
        assertThat(result.getStatus()).isEqualTo(AccountStatus.APPROVED);

        verify(accountService).approveAccount(accountId);
    }

    @Test
    void approveAccount_AsNonAdmin_ThrowsException() {
        // Given
        Long accountId = 1L;
        SecurityContextHolder.getContext().setAuthentication(userAuth);

        // When/Then
        assertThatThrownBy(() -> accountMutation.approveAccount(accountId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only administrators can approve accounts");
    }

    @Test
    void approveAccount_WithUnauthenticatedUser_ThrowsException() {
        // Given - no authentication set

        // When/Then
        assertThatThrownBy(() -> accountMutation.approveAccount(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User must be authenticated");
    }

    // ==================== rejectAccount() tests ====================

    @Test
    void rejectAccount_AsAdmin_RejectsAccount() {
        // Given
        Long accountId = 1L;
        String reason = "Invalid price";
        SecurityContextHolder.getContext().setAuthentication(adminAuth);
        testAccount.setStatus(AccountStatus.REJECTED);
        when(accountService.rejectAccount(accountId, reason)).thenReturn(testAccount);

        // When
        Account result = accountMutation.rejectAccount(accountId, reason);

        // Then
        assertThat(result.getStatus()).isEqualTo(AccountStatus.REJECTED);

        verify(accountService).rejectAccount(accountId, reason);
    }

    @Test
    void rejectAccount_AsNonAdmin_ThrowsException() {
        // Given
        Long accountId = 1L;
        SecurityContextHolder.getContext().setAuthentication(userAuth);

        // When/Then
        assertThatThrownBy(() -> accountMutation.rejectAccount(accountId, "reason"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only administrators can reject accounts");
    }

    @Test
    void rejectAccount_WithUnauthenticatedUser_ThrowsException() {
        // Given - no authentication set

        // When/Then
        assertThatThrownBy(() -> accountMutation.rejectAccount(1L, "reason"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User must be authenticated");
    }
}
