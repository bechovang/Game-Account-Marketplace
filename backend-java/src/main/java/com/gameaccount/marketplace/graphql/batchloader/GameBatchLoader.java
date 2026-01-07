package com.gameaccount.marketplace.graphql.batchloader;

import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.BatchLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Batch loader for Game entities to prevent N+1 query problems.
 *
 * When GraphQL queries multiple accounts with their games,
 * this loader batches all game ID lookups into a single query:
 *
 * Without DataLoader: 1 query for accounts + N queries for games = N+1 queries
 * With DataLoader: 1 query for accounts + 1 query for games = 2 queries
 *
 * Usage:
 * <pre>
 * DataLoader<Long, Game> gameLoader = DataLoader.newDataLoader(new GameBatchLoader(gameRepository));
 * gameLoader.loadMany(Arrays.asList(1L, 2L, 3L)); // Single query for games 1, 2, 3
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GameBatchLoader implements BatchLoader<Long, Game> {

    private final GameRepository gameRepository;

    /**
     * Executor for async batch loading.
     * Uses a cached thread pool for efficient concurrent processing.
     */
    private static final Executor executor = Executors.newCachedThreadPool(r -> {
        Thread thread = new Thread(r);
        thread.setName("game-batch-loader-" + thread.getId());
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Batch load games by IDs.
     * Called by DataLoader when multiple game IDs need to be loaded.
     *
     * @param gameIds List of game IDs to load
     * @return CompletableFuture containing list of games (in same order as IDs)
     */
    @Override
    public CompletableFuture<List<Game>> load(List<Long> gameIds) {
        log.debug("Batch loading {} games", gameIds.size());

        return CompletableFuture.supplyAsync(() -> {
            List<Game> games = gameRepository.findAllById(gameIds);
            log.debug("Batch loaded {} games (requested: {})", games.size(), gameIds.size());
            return games;
        }, executor);
    }
}
