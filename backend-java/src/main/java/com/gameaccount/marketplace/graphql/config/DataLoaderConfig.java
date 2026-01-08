package com.gameaccount.marketplace.graphql.config;

import com.gameaccount.marketplace.graphql.batchloader.FavoriteBatchLoader;
import com.gameaccount.marketplace.graphql.batchloader.GameBatchLoader;
import com.gameaccount.marketplace.graphql.batchloader.UserBatchLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for GraphQL DataLoader registration with Spring Boot 3.
 *
 * DataLoaders batch queries to related entities, preventing N+1 query problems.
 *
 * This configuration creates DataLoaderRegistry as a Spring bean that can be
 * injected into GraphQL resolvers. Spring GraphQL supports DataLoader integration
 * through proper @SchemaMapping annotations with DataLoader parameters.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataLoaderConfig {

    private final UserBatchLoader userBatchLoader;
    private final GameBatchLoader gameBatchLoader;
    private final FavoriteBatchLoader favoriteBatchLoader;

    /**
     * Build DataLoaderRegistry bean with all registered batch loaders.
     * Spring GraphQL supports DataLoader integration through @SchemaMapping
     * annotations with DataLoader parameters for proper batching.
     *
     * @return DataLoaderRegistry with user, game, and favorite loaders
     */
    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry registry = new DataLoaderRegistry();

        // Register User batch loader
        DataLoader<Long, com.gameaccount.marketplace.entity.User> userDataLoader =
                DataLoader.newDataLoader(userBatchLoader);
        registry.register("userLoader", userDataLoader);

        // Register Game batch loader
        DataLoader<Long, com.gameaccount.marketplace.entity.Game> gameDataLoader =
                DataLoader.newDataLoader(gameBatchLoader);
        registry.register("gameLoader", gameDataLoader);

        // Register Favorite batch loader
        DataLoader<Long, Boolean> favoriteDataLoader =
                DataLoader.newDataLoader(favoriteBatchLoader);
        registry.register("favoriteLoader", favoriteDataLoader);

        log.debug("DataLoaderRegistry created with userLoader, gameLoader, and favoriteLoader");

        return registry;
    }
}
