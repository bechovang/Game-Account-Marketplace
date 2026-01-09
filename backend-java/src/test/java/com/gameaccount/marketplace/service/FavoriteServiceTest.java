package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Favorite;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.repository.FavoriteRepository;
import com.gameaccount.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FavoriteService.
 * Tests all business logic for adding, removing, and retrieving favorites.
 */
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private User testUser;
    private Account testAccount;
    private Favorite testFavorite;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .fullName("Test User")
                .password("hashedPassword")
                .role(User.Role.BUYER)
                .status(User.UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        testAccount = Account.builder()
                .id(1L)
                .title("Level 100 Account")
                .description("High-level account")
                .price(1000.0)
                .status(Account.AccountStatus.APPROVED)
                .seller(testUser)
                .build();

        testFavorite = Favorite.builder()
                .id(1L)
                .user(testUser)
                .account(testAccount)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void addToFavorites_Success() {
        // Given
        Long accountId = 1L;
        Long userId = 1L;

        when(accountRepository.findByIdWithRelationships(accountId)).thenReturn(Optional.of(testAccount));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(favoriteRepository.existsByUserIdAndAccountId(userId, accountId)).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(invocation -> {
            Favorite fav = invocation.getArgument(0);
            fav.setId(1L);
            fav.setCreatedAt(LocalDateTime.now());
            return fav;
        });

        // When
        Favorite result = favoriteService.addToFavorites(accountId, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getAccount()).isEqualTo(testAccount);

        verify(accountRepository).findByIdWithRelationships(accountId);
        verify(userRepository).findById(userId);
        verify(favoriteRepository).existsByUserIdAndAccountId(userId, accountId);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addToFavorites_AlreadyFavorited_ThrowsException() {
        // Given
        Long accountId = 1L;
        Long userId = 1L;

        when(accountRepository.findByIdWithRelationships(accountId)).thenReturn(Optional.of(testAccount));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(favoriteRepository.existsByUserIdAndAccountId(userId, accountId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> favoriteService.addToFavorites(accountId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Account is already in favorites");

        verify(accountRepository).findByIdWithRelationships(accountId);
        verify(userRepository).findById(userId);
        verify(favoriteRepository).existsByUserIdAndAccountId(userId, accountId);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void addToFavorites_AccountNotFound_ThrowsException() {
        // Given
        Long accountId = 999L;
        Long userId = 1L;

        when(accountRepository.findByIdWithRelationships(accountId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> favoriteService.addToFavorites(accountId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found")
                .hasMessageContaining("999");

        verify(accountRepository).findByIdWithRelationships(accountId);
        verify(userRepository, never()).findById(any());
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void addToFavorites_UserNotFound_ThrowsException() {
        // Given
        Long accountId = 1L;
        Long userId = 999L;

        when(accountRepository.findByIdWithRelationships(accountId)).thenReturn(Optional.of(testAccount));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> favoriteService.addToFavorites(accountId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining("999");

        verify(accountRepository).findByIdWithRelationships(accountId);
        verify(userRepository).findById(userId);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void removeFromFavorites_Success() {
        // Given
        Long accountId = 1L;
        Long userId = 1L;

        when(favoriteRepository.existsByUserIdAndAccountId(userId, accountId)).thenReturn(true);
        doNothing().when(favoriteRepository).deleteByUserIdAndAccountId(userId, accountId);

        // When
        favoriteService.removeFromFavorites(accountId, userId);

        // Then
        verify(favoriteRepository).existsByUserIdAndAccountId(userId, accountId);
        verify(favoriteRepository).deleteByUserIdAndAccountId(userId, accountId);
    }

    @Test
    void removeFromFavorites_NotFavorited_ThrowsException() {
        // Given
        Long accountId = 1L;
        Long userId = 1L;

        when(favoriteRepository.existsByUserIdAndAccountId(userId, accountId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> favoriteService.removeFromFavorites(accountId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Favorite not found");

        verify(favoriteRepository).existsByUserIdAndAccountId(userId, accountId);
        verify(favoriteRepository, never()).deleteByUserIdAndAccountId(any(), any());
    }

    @Test
    void getUserFavorites_ReturnsAccountList() {
        // Given
        Long userId = 1L;

        Account account1 = Account.builder()
                .id(1L)
                .title("Account 1")
                .description("Description 1")
                .price(100.0)
                .build();

        Account account2 = Account.builder()
                .id(2L)
                .title("Account 2")
                .description("Description 2")
                .price(200.0)
                .build();

        Favorite fav1 = Favorite.builder()
                .id(1L)
                .user(testUser)
                .account(account1)
                .createdAt(LocalDateTime.now())
                .build();

        Favorite fav2 = Favorite.builder()
                .id(2L)
                .user(testUser)
                .account(account2)
                .createdAt(LocalDateTime.now())
                .build();

        when(favoriteRepository.findByUserIdWithAccount(userId)).thenReturn(Arrays.asList(fav1, fav2));

        // When
        List<Account> result = favoriteService.getUserFavorites(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Account 1");
        assertThat(result.get(1).getTitle()).isEqualTo("Account 2");

        verify(favoriteRepository).findByUserIdWithAccount(userId);
    }

    @Test
    void getUserFavorites_EmptyList() {
        // Given
        Long userId = 1L;

        when(favoriteRepository.findByUserIdWithAccount(userId)).thenReturn(List.of());

        // When
        List<Account> result = favoriteService.getUserFavorites(userId);

        // Then
        assertThat(result).isEmpty();
        verify(favoriteRepository).findByUserIdWithAccount(userId);
    }

    @Test
    void isFavorited_ReturnsTrue() {
        // Given
        Long accountId = 1L;
        Long userId = 1L;

        when(favoriteRepository.existsByUserIdAndAccountId(userId, accountId)).thenReturn(true);

        // When
        boolean result = favoriteService.isFavorited(accountId, userId);

        // Then
        assertThat(result).isTrue();
        verify(favoriteRepository).existsByUserIdAndAccountId(userId, accountId);
    }

    @Test
    void isFavorited_ReturnsFalse() {
        // Given
        Long accountId = 1L;
        Long userId = 1L;

        when(favoriteRepository.existsByUserIdAndAccountId(userId, accountId)).thenReturn(false);

        // When
        boolean result = favoriteService.isFavorited(accountId, userId);

        // Then
        assertThat(result).isFalse();
        verify(favoriteRepository).existsByUserIdAndAccountId(userId, accountId);
    }
}
