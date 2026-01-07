package com.gameaccount.marketplace.graphql.config;

import com.gameaccount.marketplace.graphql.batchloader.GameBatchLoader;
import com.gameaccount.marketplace.graphql.batchloader.UserBatchLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for GraphQL DataLoader registration with Spring Boot 3.
 *
 * DataLoaders batch queries to related entities, preventing N+1 query problems.
 *
 * NOTE: Full DataLoader integration with Spring Boot 3 GraphQL requires
 * custom data fetchers or field resolvers to manually invoke the DataLoaders.
 * This configuration provides the registry structure that can be used
 * by field-level resolvers when implementing DataLoader batching.
 *
 * For full DataLoader integration in Spring Boot 3 GraphQL, you would:
 * 1. Create this registry as a bean
 * 2. Inject it into custom field resolvers
 * 3. Manually call DataLoader.load() for each relationship
 *
 * Alternative: Use GraphQL Java Tools (graphql-java-tools) which has
 * built-in DataLoader support, but that conflicts with Spring Boot 3's
 * native GraphQL support.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataLoaderConfig {

    private final UserBatchLoader userBatchLoader;
    private final GameBatchLoader gameBatchLoader;

    /**
     * Build DataLoaderRegistry with all registered batch loaders.
     * This registry can be injected into custom field resolvers that
     * manually handle DataLoader batching for relationships.
     *
     * Note: Spring Boot 3's GraphQL framework does not automatically
     * invoke DataLoaders - you must create custom field resolvers
     * that use this registry to batch load relationships.
     *
     * @return DataLoaderRegistry with user and game loaders
     */
    public DataLoaderRegistry buildDataLoaderRegistry() {
        DataLoaderRegistry registry = new DataLoaderRegistry();

        // Register User batch loader
        DataLoader<Long, com.gameaccount.marketplace.entity.User> userDataLoader =
                DataLoader.newDataLoader(userBatchLoader);
        registry.register("userLoader", userDataLoader);

        // Register Game batch loader
        DataLoader<Long, com.gameaccount.marketplace.entity.Game> gameDataLoader =
                DataLoader.newDataLoader(gameBatchLoader);
        registry.register("gameLoader", gameDataLoader);

        log.debug("DataLoaderRegistry created with userLoader and gameLoader");

        return registry;
    }
}
