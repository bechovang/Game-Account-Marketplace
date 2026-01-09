package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Favorite;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.repository.FavoriteRepository;
import com.gameaccount.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing user favorites.
 * Handles adding, removing, and retrieving favorite accounts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /**
     * Add an account to user's favorites.
     *
     * @param accountId ID of account to favorite
     * @param userId ID of user favoriting the account
     * @return Created Favorite entity
     * @throws ResourceNotFoundException if account or user not found
     * @throws BusinessException if account is already favorited
     */
    @CacheEvict(value = "favorites", key = "#userId")
    public Favorite addToFavorites(Long accountId, Long userId) {
        log.debug("Adding account {} to favorites for user {}", accountId, userId);

        // Verify account exists and load with relationships
        Account account = accountRepository.findByIdWithRelationships(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndAccountId(userId, accountId)) {
            throw new BusinessException("Account is already in favorites");
        }

        // Create favorite
        Favorite favorite = Favorite.builder()
                .user(user)
                .account(account)
                .build();

        return favoriteRepository.save(favorite);
    }

    /**
     * Remove an account from user's favorites.
     *
     * @param accountId ID of account to unfavorite
     * @param userId ID of user removing the favorite
     * @throws ResourceNotFoundException if favorite not found
     */
    @CacheEvict(value = "favorites", key = "#userId")
    public void removeFromFavorites(Long accountId, Long userId) {
        log.debug("Removing account {} from favorites for user {}", accountId, userId);

        // Check if favorite exists
        if (!favoriteRepository.existsByUserIdAndAccountId(userId, accountId)) {
            throw new ResourceNotFoundException("Favorite not found for userId: " + userId + " and accountId: " + accountId);
        }

        // Delete favorite
        favoriteRepository.deleteByUserIdAndAccountId(userId, accountId);
    }

    /**
     * Get all favorited accounts for a user.
     * Uses JOIN FETCH query to prevent N+1 query problem.
     *
     * @param userId ID of user
     * @return List of favorited Account objects
     */
    @Cacheable(value = "favorites", key = "#userId")
    @Transactional(readOnly = true)
    public List<Account> getUserFavorites(Long userId) {
        log.debug("Getting favorites for user {}", userId);

        // Use JOIN FETCH query to prevent N+1 problem (loads accounts in single query)
        List<Favorite> favorites = favoriteRepository.findByUserIdWithAccount(userId);

        // Extract accounts from favorites (already loaded, no additional queries)
        return favorites.stream()
                .map(Favorite::getAccount)
                .collect(Collectors.toList());
    }

    /**
     * Check if an account is favorited by a user.
     *
     * @param accountId ID of account
     * @param userId ID of user
     * @return true if favorited, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isFavorited(Long accountId, Long userId) {
        return favoriteRepository.existsByUserIdAndAccountId(userId, accountId);
    }
}
