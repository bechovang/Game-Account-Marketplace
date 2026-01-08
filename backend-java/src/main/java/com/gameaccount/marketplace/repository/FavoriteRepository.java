package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Favorite entity.
 * Provides methods for managing user favorites.
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * Find all favorites for a specific user.
     *
     * @param userId the user ID
     * @return list of favorites
     */
    List<Favorite> findByUserId(Long userId);

    /**
     * Find all favorites for a specific user with account eagerly loaded.
     * Uses JOIN FETCH to prevent N+1 query problem.
     *
     * @param userId the user ID
     * @return list of favorites with accounts loaded
     */
    @Query("SELECT f FROM Favorite f JOIN FETCH f.account WHERE f.user.id = :userId")
    List<Favorite> findByUserIdWithAccount(Long userId);

    /**
     * Check if a specific favorite exists.
     *
     * @param userId the user ID
     * @param accountId the account ID
     * @return true if favorite exists, false otherwise
     */
    boolean existsByUserIdAndAccountId(Long userId, Long accountId);

    /**
     * Delete a specific favorite.
     *
     * @param userId the user ID
     * @param accountId the account ID
     */
    void deleteByUserIdAndAccountId(Long userId, Long accountId);

    /**
     * Find a specific favorite.
     *
     * @param userId the user ID
     * @param accountId the account ID
     * @return optional containing the favorite if found
     */
    Optional<Favorite> findByUserIdAndAccountId(Long userId, Long accountId);

    /**
     * Count favorites for a specific account.
     *
     * @param accountId the account ID
     * @return count of favorites
     */
    long countByAccountId(Long accountId);

    /**
     * Find all account IDs that are favorited by a specific user.
     * Used for batch loading favorite status.
     *
     * @param userId the user ID
     * @param accountIds list of account IDs to check
     * @return list of account IDs that are favorited
     */
    @Query("SELECT f.account.id FROM Favorite f WHERE f.user.id = :userId AND f.account.id IN :accountIds")
    List<Long> findFavoritedAccountIdsByUserIdAndAccountIds(Long userId, List<Long> accountIds);
}
