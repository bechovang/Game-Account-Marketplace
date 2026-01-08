package com.gameaccount.marketplace.graphql.batchloader;

import com.gameaccount.marketplace.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.BatchLoader;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Batch loader for favorite status to prevent N+1 query problems.
 *
 * When GraphQL queries multiple accounts with their isFavorited field,
 * this loader batches all favorite status checks into a single query.
 *
 * Without DataLoader: 1 query for accounts + N queries for isFavorited = N+1 queries
 * With DataLoader: 1 query for accounts + 1 query for favorite status = 2 queries
 *
 * Usage:
 * <pre>
 * DataLoader<Long, Boolean> favoriteLoader = DataLoader.newDataLoader(new FavoriteBatchLoader(favoriteRepository));
 * favoriteLoader.loadMany(Arrays.asList(1L, 2L, 3L)); // Single query to check if accounts 1, 2, 3 are favorited
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FavoriteBatchLoader implements BatchLoader<Long, Boolean> {

    private final FavoriteRepository favoriteRepository;

    /**
     * Executor for async batch loading.
     * Uses a cached thread pool for efficient concurrent processing.
     */
    private static final Executor executor = Executors.newCachedThreadPool(r -> {
        Thread thread = new Thread(r);
        thread.setName("favorite-batch-loader-" + thread.getId());
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Batch load favorite status for accounts.
     * Called by DataLoader when multiple account IDs need to be checked.
     *
     * @param accountIds List of account IDs to check
     * @return CompletableFuture containing list of Boolean values (in same order as IDs)
     */
    @Override
    public CompletableFuture<List<Boolean>> load(List<Long> accountIds) {
        log.debug("Batch loading favorite status for {} accounts", accountIds.size());

        return CompletableFuture.supplyAsync(() -> {
            // Get authenticated user ID
            Long userId = getUserIdIfAuthenticated();

            // If no authenticated user, return all false
            if (userId == null) {
                log.debug("No authenticated user, returning all false for {} accounts", accountIds.size());
                return accountIds.stream()
                        .map(id -> Boolean.FALSE)
                        .collect(Collectors.toList());
            }

            // Batch query to find which accounts are favorited
            List<Long> favoritedAccountIds = favoriteRepository.findFavoritedAccountIdsByUserIdAndAccountIds(
                    userId, accountIds);
            Set<Long> favoritedSet = favoritedAccountIds.stream()
                    .collect(Collectors.toSet());

            log.debug("Batch loaded favorite status: {} of {} accounts are favorited by user {}",
                    favoritedSet.size(), accountIds.size(), userId);

            // Return boolean list in same order as input account IDs
            return accountIds.stream()
                    .map(favoritedSet::contains)
                    .collect(Collectors.toList());
        }, executor);
    }

    /**
     * Get authenticated user ID if available, returns null if not authenticated.
     *
     * @return User ID or null if not authenticated
     */
    private Long getUserIdIfAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            log.error("Failed to parse user ID from authentication: {}", authentication.getName());
            return null;
        }
    }
}
