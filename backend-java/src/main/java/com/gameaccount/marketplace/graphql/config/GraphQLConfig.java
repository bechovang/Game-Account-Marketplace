package com.gameaccount.marketplace.graphql.config;

import graphql.execution.instrumentation.Instrumentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * GraphQL configuration with DataLoader integration and query protection.
 *
 * Spring GraphQL supports DataLoader integration through:
 * 1. DataLoaderRegistry bean for field resolvers
 * 2. @SchemaMapping with DataLoader parameters for batching
 * 3. Automatic batching within single GraphQL operations
 */
@Slf4j
@Configuration
public class GraphQLConfig {

    /**
     * Configure GraphQL runtime wiring.
     * Additional wiring configuration can be added here if needed.
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.build();
    }

    /**
     * GraphQL instrumentation for query complexity and depth analysis.
     * Protects against malicious or overly complex queries.
     */
    @Bean
    public Instrumentation queryComplexityInstrumentation() {
        return new QueryComplexityInstrumentation(1000, 10); // maxComplexity: 1000, maxDepth: 10
    }
}
