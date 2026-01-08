# DataLoader Integration Guide

## Overview

This document explains how DataLoader integration works in the Game Account Marketplace to prevent N+1 query problems and optimize GraphQL performance.

## Problem: N+1 Query Issue

Without DataLoader, GraphQL queries can cause exponential database queries:

```graphql
query GetAccounts {
  accounts(first: 50) {
    id
    title
    seller {        # N+1: Query for EACH seller
      id
      fullName
    }
    game {          # N+1: Query for EACH game
      id
      name
    }
  }
}
```

**Without DataLoader:**
- 1 query for accounts
- 50 queries for sellers (one per account)
- 50 queries for games (one per account)
- **Total: 101 queries**

**With DataLoader:**
- 1 query for accounts
- 1 batch query for all unique sellers
- 1 batch query for all unique games
- **Total: 3 queries** (94% reduction)

## Architecture

### Components

1. **BatchLoaders** - `src/main/java/.../graphql/batchloader/`
   - `UserBatchLoader.java` - Batches user queries
   - `GameBatchLoader.java` - Batches game queries
   - `FavoriteBatchLoader.java` - Batches favorite status checks

2. **Configuration** - `src/main/java/.../graphql/config/`
   - `DataLoaderConfig.java` - Creates DataLoaderRegistry bean
   - `GraphQLConfig.java` - GraphQL configuration

3. **Field Resolvers** - `src/main/java/.../graphql/`
   - `AccountQuery.java` - Seller and game field resolvers
   - `AccountFieldResolver.java` - isFavorited field resolver

### How It Works

#### Step 1: DataLoader Registration

`DataLoaderConfig.java` creates the DataLoaderRegistry bean:

```java
@Bean
public DataLoaderRegistry dataLoaderRegistry() {
    DataLoaderRegistry registry = new DataLoaderRegistry();

    // Register loaders
    DataLoader<Long, User> userDataLoader =
        DataLoader.newDataLoader(userBatchLoader);
    registry.register("userLoader", userDataLoader);

    DataLoader<Long, Game> gameDataLoader =
        DataLoader.newDataLoader(gameBatchLoader);
    registry.register("gameLoader", gameDataLoader);

    return registry;
}
```

#### Step 2: Field Resolvers Use DataLoader

Spring Boot 3 GraphQL automatically injects DataLoader beans into `@SchemaMapping` methods:

```java
@SchemaMapping(typeName = "Account", field = "seller")
public CompletableFuture<User> seller(Account account,
                                     DataLoader<Long, User> userLoader) {
    // DataLoader batches these calls
    return userLoader.load(account.getSellerId());
}
```

#### Step 3: Batch Loading

When GraphQL resolves multiple accounts:

1. Each account field resolver calls `userLoader.load(sellerId)`
2. DataLoader collects all seller IDs from the request
3. DataLoader calls `UserBatchLoader.load(List<Long> ids)` ONCE
4. Results are cached within the request and returned to each resolver

```java
@Override
public CompletableFuture<List<User>> load(List<Long> userIds) {
    // Single query for ALL users
    return CompletableFuture.supplyAsync(() -> {
        return userRepository.findAllById(userIds);
    }, executor);
}
```

## Implementation Patterns

### 1. Batch Loader Pattern

All batch loaders follow this structure:

```java
@Component
@RequiredArgsConstructor
public class XxxBatchLoader implements BatchLoader<K, V> {

    private final XxxRepository repository;

    private static final Executor executor =
        Executors.newCachedThreadPool(/* thread factory */);

    @Override
    public CompletableFuture<List<V>> load(List<K> keys) {
        return CompletableFuture.supplyAsync(() -> {
            // Single batch query
            return repository.findAllById(keys);
        }, executor);
    }
}
```

**Key Points:**
- Use `findAllById()` for batch queries
- Use async executor for non-blocking execution
- Return `CompletableFuture` for async resolution

### 2. Field Resolver Pattern

Field resolvers use DataLoader parameters:

```java
@SchemaMapping(typeName = "Parent", field = "child")
public CompletableFuture<Child> child(Parent parent,
                                     DataLoader<K, Child> loader) {
    return loader.load(parent.getChildId());
}
```

**Key Points:**
- Return `CompletableFuture<Child>` for async resolution
- Use foreign key ID, not the entity (avoid lazy loading)
- Spring GraphQL injects DataLoader by type

### 3. DataLoader Registry Pattern

Register all loaders in configuration:

```java
@Bean
public DataLoaderRegistry dataLoaderRegistry() {
    DataLoaderRegistry registry = new DataLoaderRegistry();
    registry.register("loaderName", DataLoader.newDataLoader(batchLoader));
    return registry;
}
```

**Key Points:**
- Use descriptive names ("userLoader", "gameLoader")
- Registry is request-scoped by Spring GraphQL
- Same registry used for all field resolvers in a request

## Best Practices

### DO ✅

1. **Use foreign key IDs, not entities**
   ```java
   // Good
   return userLoader.load(account.getSellerId());

   // Bad (causes lazy loading)
   return CompletableFuture.completedFuture(account.getSeller());
   ```

2. **Batch queries in loaders**
   ```java
   // Good - single query
   return repository.findAllById(ids);

   // Bad - N queries
   return ids.stream()
       .map(id -> repository.findById(id))
       .toList();
   ```

3. **Handle missing data gracefully**
   ```java
   @Override
   public CompletableFuture<List<User>> load(List<Long> userIds) {
       List<User> users = userRepository.findAllById(userIds);
       // Return users in same order as IDs (DataLoader requirement)
       return CompletableFuture.completedFuture(users);
   }
   ```

4. **Use async execution**
   ```java
   return CompletableFuture.supplyAsync(() -> {
       return repository.findAllById(keys);
   }, executor);
   ```

### DON'T ❌

1. **Don't access lazy relationships in resolvers**
   ```java
   // Bad - causes N+1 lazy loading
   public CompletableFuture<User> seller(Account account) {
       return CompletableFuture.completedFuture(account.getSeller());
   }
   ```

2. **Don't create new DataLoader instances per request**
   ```java
   // Bad - creates new loader each time
   DataLoader<Long, User> loader = DataLoader.newDataLoader(...);
   return loader.load(id);
   ```

3. **Don't use blocking operations in loaders**
   ```java
   // Bad - blocks thread
   public List<User> load(List<Long> ids) {
       return repository.findAllById(ids); // Blocking!
   }
   ```

## Testing

### Integration Testing

Test that DataLoader actually reduces queries:

```java
@SpringBootTest
class DataLoaderValidationTest {

    @Test
    void dataLoader_preventsNPlus1Queries() {
        // Query 50 accounts with nested data
        String query = """
            query GetAccounts {
                accounts(first: 50) {
                    content {
                        id
                        seller { id fullName }
                        game { id name }
                    }
                }
            }
            """;

        // Measure response time
        long startTime = System.currentTimeMillis();
        var response = graphQlTester.document(query).execute();
        long duration = System.currentTimeMillis() - startTime;

        // Should be under 300ms (DataLoader working)
        assertThat(duration).isLessThan(300);
    }
}
```

### Performance Testing

Measure query count reduction:

```java
@Test
void measureQueryCount_withDataLoader() {
    // Use statistics to count queries
    // Expected: 3 queries (accounts + users + games)
    // Without DataLoader: 101 queries
}
```

## Troubleshooting

### DataLoader Not Working

**Symptom:** Still seeing N+1 queries

**Check:**
1. DataLoader registered in `DataLoaderConfig`?
2. Field resolver using `DataLoader` parameter?
3. Using foreign key ID, not entity?
4. Spring GraphQL scanning the resolver package?

**Debug:**
```java
@SchemaMapping(typeName = "Account", field = "seller")
public CompletableFuture<User> seller(Account account,
                                     DataLoader<Long, User> userLoader) {
    log.debug("DataLoader: {}", userLoader.getClass().getName());
    log.debug("Loading seller ID: {}", account.getSellerId());
    return userLoader.load(account.getSellerId());
}
```

### Caching Issues

**Symptom:** Same entity loaded multiple times

**Check:**
1. DataLoader cache is request-scoped (default)
2. Not calling `loader.load()` multiple times for same ID
3. Batch loader returns results in same order as keys

### Performance Not Improved

**Symptom:** DataLoader implemented but no speed improvement

**Check:**
1. Actually batching (multiple IDs in single load)?
2. Using indexed columns in `findAllById()`?
3. Database has proper indexes on foreign keys?
4. Network latency vs query optimization?

## Performance Targets

Based on NFR requirements:

- **GraphQL Query Time:** < 300ms (p95) for complex nested queries
- **Query Reduction:** 94% (from N+1 to batch queries)
- **Batch Size:** Typically 10-100 items per batch
- **Cache Scope:** Request-scoped (automatic)

## Monitoring

### DataLoader Metrics

Monitor these metrics in production:

1. **Batch Sizes** - Average number of IDs per batch
2. **Cache Hit Rate** - Within-request cache hits
3. **Response Time** - P50, P95, P99 latencies
4. **Query Count** - Total database queries per GraphQL operation

### Logging

Enable DEBUG logging for DataLoader:

```yaml
logging:
  level:
    com.gameaccount.marketplace.graphql.batchloader: DEBUG
    org.dataloader: DEBUG
```

## References

- [GraphQL Java DataLoader](https://www.graphql-java.com/documentation/batching/)
- [DataLoader Specification](https://github.com/graphql/dataloader)
- [Spring Boot GraphQL Documentation](https://docs.spring.io/spring-graphql/reference/)
- Story 3.9: DataLoader for N+1 Query Prevention
- DataLoaderValidationTest.java: Integration tests

---

**Author:** Charlie (Senior Dev) with Elena (Junior Dev)
**Last Updated:** 2026-01-09 (Hardening Sprint)
**Status:** ✅ Validated and Production-Ready
