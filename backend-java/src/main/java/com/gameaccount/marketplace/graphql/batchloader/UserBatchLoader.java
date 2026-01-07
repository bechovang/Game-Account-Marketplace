package com.gameaccount.marketplace.graphql.batchloader;

import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.BatchLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Batch loader for User entities to prevent N+1 query problems.
 *
 * When GraphQL queries multiple accounts with their sellers,
 * this loader batches all user ID lookups into a single query:
 *
 * Without DataLoader: 1 query for accounts + N queries for sellers = N+1 queries
 * With DataLoader: 1 query for accounts + 1 query for sellers = 2 queries
 *
 * Usage:
 * <pre>
 * DataLoader<Long, User> userLoader = DataLoader.newDataLoader(new UserBatchLoader(userRepository));
 * userLoader.loadMany(Arrays.asList(1L, 2L, 3L)); // Single query for users 1, 2, 3
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserBatchLoader implements BatchLoader<Long, User> {

    private final UserRepository userRepository;

    /**
     * Executor for async batch loading.
     * Uses a cached thread pool for efficient concurrent processing.
     */
    private static final Executor executor = Executors.newCachedThreadPool(r -> {
        Thread thread = new Thread(r);
        thread.setName("user-batch-loader-" + thread.getId());
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Batch load users by IDs.
     * Called by DataLoader when multiple user IDs need to be loaded.
     *
     * @param userIds List of user IDs to load
     * @return CompletableFuture containing list of users (in same order as IDs)
     */
    @Override
    public CompletableFuture<List<User>> load(List<Long> userIds) {
        log.debug("Batch loading {} users", userIds.size());

        return CompletableFuture.supplyAsync(() -> {
            List<User> users = userRepository.findAllById(userIds);
            log.debug("Batch loaded {} users (requested: {})", users.size(), userIds.size());
            return users;
        }, executor);
    }
}
