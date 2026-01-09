package com.gameaccount.marketplace.graphql.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for GraphQL DataLoader registration with Spring Boot 3.
 *
 * NOTE: DataLoader support temporarily disabled due to API incompatibility.
 * Entities are now fetched directly in @SchemaMapping methods.
 * TODO: Re-enable DataLoader batching after upgrading to Spring Boot 3.3+
 */
@Slf4j
@Configuration
public class DataLoaderConfig {
    // DataLoader configuration removed - will be re-implemented with Spring Boot 3.3+
}
