# Database Documentation

## Overview

The Game Account Marketplace uses a MySQL 8.0 database with JPA/Hibernate ORM. The database stores user accounts, game listings, favorites, and related data for the marketplace platform.

---

## Database Configuration

### Connection Settings

| Property | Value | Description |
|----------|-------|-------------|
| Database | MySQL 8.0 | Primary database engine |
| Host | `localhost:3306` | Configurable via `DB_HOST`, `DB_PORT` |
| Name | `gameaccount_marketplace` | Configurable via `DB_NAME` |
| Username | `appuser` | Configurable via `DB_USERNAME` |
| Password | `apppassword` | Configurable via `DB_PASSWORD` |

### JPA/Hibernate Settings

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Auto-update schema
    show-sql: true      # Log SQL queries
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

### Docker Compose

The database runs in Docker using `docker-compose.yml`:

```yaml
mysql:
  image: mysql:8.0
  environment:
    MYSQL_DATABASE: gameaccount_marketplace
    MYSQL_USER: appuser
    MYSQL_PASSWORD: apppassword
  ports:
    - "3306:3306"
  volumes:
    - mysql-data:/var/lib/mysql
```

---

## Entity Schema

### 1. Users Table (`users`)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL | User email address |
| `password` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `full_name` | VARCHAR(100) | NULLABLE | User's full name |
| `avatar` | VARCHAR(255) | NULLABLE | Avatar URL |
| `role` | ENUM | NOT NULL | BUYER, SELLER, ADMIN |
| `status` | ENUM | NOT NULL | ACTIVE, BANNED, SUSPENDED |
| `balance` | DOUBLE | DEFAULT 0.0 | Account balance |
| `rating` | DOUBLE | DEFAULT 0.0 | User rating (0-5) |
| `total_reviews` | INT | DEFAULT 0 | Number of reviews |
| `created_at` | TIMESTAMP | NOT NULL | Account creation time |
| `updated_at` | TIMESTAMP | NOT NULL | Last update time |

**Entity Location**: `backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java:1`

### 2. Games Table (`games`)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `name` | VARCHAR(100) | NOT NULL | Game name |
| `slug` | VARCHAR(100) | UNIQUE, NOT NULL | URL-friendly identifier |
| `description` | VARCHAR(1000) | NULLABLE | Game description |
| `icon_url` | VARCHAR(255) | NULLABLE | Game icon URL |
| `account_count` | INT | DEFAULT 0 | Number of approved accounts |
| `created_at` | TIMESTAMP | NOT NULL | Creation time |

**Entity Location**: `backend-java/src/main/java/com/gameaccount/marketplace/entity/Game.java:1`

### 3. Accounts Table (`accounts`)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `seller_id` | BIGINT | FOREIGN KEY (users.id) | Seller reference |
| `game_id` | BIGINT | FOREIGN KEY (games.id) | Game reference |
| `title` | VARCHAR(200) | NOT NULL | Listing title |
| `description` | VARCHAR(2000) | NULLABLE | Account description |
| `level` | INT | NULLABLE | Account level |
| `player_rank` | VARCHAR(50) | NULLABLE | Player rank |
| `price` | DOUBLE | NOT NULL | Listing price |
| `status` | ENUM | NOT NULL | PENDING, APPROVED, REJECTED, SOLD |
| `views_count` | INT | DEFAULT 0 | View counter |
| `is_featured` | BOOLEAN | DEFAULT false | Featured listing flag |
| `created_at` | TIMESTAMP | NOT NULL | Creation time |
| `updated_at` | TIMESTAMP | NOT NULL | Last update time |

**Indexes**:
- `idx_account_seller` - on `seller_id`
- `idx_account_game` - on `game_id`
- `idx_account_status` - on `status`
- `idx_account_price` - on `price`
- `idx_account_featured` - on `is_featured`
- `idx_account_level` - on `level`
- `idx_account_created_at` - on `created_at`
- `idx_account_status_featured` - composite on `status, is_featured`
- `idx_account_title` - on `title`

**Entity Location**: `backend-java/src/main/java/com/gameaccount/marketplace/entity/Account.java:1`

### 4. Account Images Table (`account_images`)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `account_id` | BIGINT | FOREIGN KEY (accounts.id) | Account reference |
| `url` | VARCHAR | NOT NULL | Image URL |

**Note**: This is an `@ElementCollection` table managed by JPA.

### 5. Favorites Table (`favorites`)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `user_id` | BIGINT | FOREIGN KEY (users.id) | User reference |
| `account_id` | BIGINT | FOREIGN KEY (accounts.id) ON DELETE CASCADE | Account reference |
| `created_at` | TIMESTAMP | NOT NULL | Creation time |

**Constraints**:
- `idx_favorite_user_account` - UNIQUE on `user_id, account_id` (prevents duplicates)
- `idx_favorite_user` - on `user_id`
- `idx_favorite_account` - on `account_id`

**Entity Location**: `backend-java/src/main/java/com/gameaccount/marketplace/entity/Favorite.java:1`

---

## Entity Relationships (ER Diagram)

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   Users     │         │   Games     │         │  Accounts   │
├─────────────┤         ├─────────────┤         ├─────────────┤
│ id (PK)     │◄────────│ id (PK)     │────────►│ id (PK)     │
│ email       │  1:N    │ name        │    1:N  │ seller_id   │
│ password    │         │ slug (UNQ)  │         │ game_id     │
│ role        │         │ icon_url    │         │ title       │
│ status      │         │ account_cnt │         │ price       │
│ balance     │         └─────────────┘         │ status      │
│ rating      │                                  │ is_featured │
└─────────────┘                                  └──────┬──────┘
       │                                                 │
       │ 1:N                                             │ 1:N
       ▼                                                 ▼
┌─────────────┐                                 ┌─────────────┐
│  Favorites  │                                 │account_images│
├─────────────┤                                 ├─────────────┤
│ id (PK)     │                                 │ account_id  │
│ user_id     │                                 │ url         │
│ account_id  │                                 └─────────────┘
│ created_at  │
└─────────────┘
```

**Relationship Types**:
- `User` → `Account`: One-to-Many (seller has many accounts)
- `Game` → `Account`: One-to-Many (game has many accounts)
- `User` → `Favorite`: One-to-Many (user has many favorites)
- `Account` → `Favorite`: One-to-Many (account can be favorited by many users)
- `Account` → `account_images`: One-to-Many (element collection)

---

## Repositories

### UserRepository

**Location**: `backend-java/src/main/java/com/gameaccount/marketplace/repository/UserRepository.java:1`

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(User.Role role);
    Page<User> searchUsers(String keyword, Pageable pageable);
}
```

### AccountRepository

**Location**: `backend-java/src/main/java/com/gameaccount/marketplace/repository/AccountRepository.java:1`

```java
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    @Query("SELECT a FROM Account a JOIN FETCH a.seller JOIN FETCH a.game WHERE a.id = :id")
    Optional<Account> findByIdWithRelations(@Param("id") Long id);

    Page<Account> findBySellerId(Long sellerId, Pageable pageable);
    Page<Account> findByGameId(Long gameId, Pageable pageable);
    Page<Account> findByStatus(Account.AccountStatus status, Pageable pageable);

    @Query("SELECT a FROM Account a JOIN FETCH a.game g WHERE a.status = :status AND a.isFeatured = :featured")
    Page<Account> findByStatusAndFeatured(@Param("status") Account.AccountStatus status,
                                          @Param("featured") boolean featured,
                                          Pageable pageable);
}
```

### GameRepository

**Location**: `backend-java/src/main/java/com/gameaccount/marketplace/repository/GameRepository.java:1`

```java
public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsBySlug(String slug);
    Optional<Game> findBySlug(String slug);
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<Game> findAll();
}
```

### FavoriteRepository

**Location**: `backend-java/src/main/java/com/gameaccount/marketplace/repository/FavoriteRepository.java:1`

```java
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f JOIN FETCH f.account JOIN FETCH f.account.game WHERE f.user.id = :userId")
    List<Favorite> findByUserIdWithRelations(@Param("userId") Long userId);

    boolean existsByUserIdAndAccountId(Long userId, Long accountId);
    void deleteByUserIdAndAccountId(Long userId, Long accountId);
}
```

---

## Caching Strategy

### Redis Configuration

The application uses Redis for caching Account data:

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutes
      cache-null-values: false
  data:
    redis:
      host: localhost
      port: 6379
```

### Cache Usage

- **AccountService**: Caches frequently accessed account listings
- **TTL**: 10 minutes default
- **Null Caching**: Disabled (prevents cache pollution)

---

## Database Seeding

### SQL Seed File

**Location**: `backend-java/src/main/resources/seed_data.sql:1`

```bash
mysql -u appuser -papppassword gameaccount_marketplace < seed_data.sql
```

**Includes**:
- 7 test users (admin, 3 sellers, 3 buyers)
- 8 games (LoL, Valorant, MLBB, Free Fire, PUBGM, Fortnite, Apex, CS2)
- 24 sample accounts
- 3 favorites

### Java Database Seeder

**Location**: `backend-java/src/main/java/com/gameaccount/marketplace/util/DatabaseSeeder.java:1`

**Usage**:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=seed
```

**Features**:
- Large-scale test data generation (100 users, 50 games, 1000 accounts, 500 favorites)
- Uses Faker library for realistic data
- Configurable batch sizes
- Memory-efficient (uses Long[] IDs instead of full entities)
- Fixed seed for reproducibility

**Clean & Re-seed**:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=seed --seed.clean=true
```

---

## Index Strategy

### Performance Indexes

The `accounts` table has comprehensive indexing for query optimization:

| Index Name | Columns | Purpose |
|------------|---------|---------|
| `idx_account_seller` | `seller_id` | Filter by seller |
| `idx_account_game` | `game_id` | Filter by game |
| `idx_account_status` | `status` | Filter by approval status |
| `idx_account_price` | `price` | Price range queries |
| `idx_account_featured` | `is_featured` | Featured listings |
| `idx_account_level` | `level` | Level-based filtering |
| `idx_account_created_at` | `created_at` | Date sorting |
| `idx_account_status_featured` | `status, is_featured` | Featured approved listings |
| `idx_account_title` | `title` | Full-text search |

---

## Audit Fields

All entities use Spring Data JPA auditing:

```java
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

- `createdAt`: Automatically set on insert, never updated
- `updatedAt`: Automatically updated on every modification

---

## Security Considerations

### Password Storage
- BCrypt encoding via `PasswordEncoder`
- Never store plain text passwords

### SQL Injection Prevention
- JPA parameterized queries
- `@Query` annotations with named parameters

### Cascade Deletes
- `favorites.account_id` has `ON DELETE CASCADE`
- Prevents orphaned records when accounts are deleted

---

## Database Backup & Recovery

### Docker Volume
```yaml
volumes:
  mysql-data:  # Persistent storage
```

### Manual Backup
```bash
docker exec gameaccount-mysql mysqldump -u appuser -papppassword gameaccount_marketplace > backup.sql
```

### Manual Restore
```bash
docker exec -i gameaccount-mysql mysql -u appuser -papppassword gameaccount_marketplace < backup.sql
```

---

## Common Queries

### Get Featured Approved Accounts
```sql
SELECT a.*, g.name as game_name, u.full_name as seller_name
FROM accounts a
JOIN games g ON a.game_id = g.id
JOIN users u ON a.seller_id = u.id
WHERE a.status = 'APPROVED' AND a.is_featured = TRUE
ORDER BY a.created_at DESC;
```

### Get User Favorites with Account Details
```sql
SELECT f.*, a.title, a.price, g.name as game_name
FROM favorites f
JOIN accounts a ON f.account_id = a.id
JOIN games g ON a.game_id = g.id
WHERE f.user_id = ?
ORDER BY f.created_at DESC;
```

### Get Seller Statistics
```sql
SELECT
    u.id,
    u.full_name,
    COUNT(a.id) as total_listings,
    SUM(CASE WHEN a.status = 'APPROVED' THEN 1 ELSE 0 END) as approved,
    SUM(CASE WHEN a.status = 'SOLD' THEN 1 ELSE 0 END) as sold,
    AVG(a.price) as avg_price
FROM users u
LEFT JOIN accounts a ON u.id = a.seller_id
WHERE u.role = 'SELLER'
GROUP BY u.id;
```

---

## GraphQL Schema Mapping

The database entities map to GraphQL types:

```graphql
type User {
  id: ID!
  email: String!
  fullName: String
  avatar: String
  role: Role!
  status: UserStatus!
  balance: Float!
  rating: Float!
  totalReviews: Int!
  createdAt: LocalDateTime!
  updatedAt: LocalDateTime!
}

type Account {
  id: ID!
  seller: User!
  game: Game!
  title: String!
  description: String
  level: Int
  rank: String
  price: Float!
  status: AccountStatus!
  viewsCount: Int!
  isFeatured: Boolean!
  images: [String!]!
  createdAt: LocalDateTime!
  updatedAt: LocalDateTime!
}
```

**Schema Location**: `backend-java/src/main/resources/graphql/schema.graphqls:1`

---

## Future Enhancements (Planned)

### Planned Entities
- **Transaction**: For purchase tracking (Epic 4)
- **Review**: For account ratings and reviews
- **Message**: For seller-buyer communication
- **Offer**: For price negotiation
- **Dispute**: For transaction conflicts

### Planned Relationships
```java
// In User entity (currently commented out)
@OneToMany(mappedBy = "seller")
private List<Account> accounts;

@OneToMany(mappedBy = "buyer")
private List<Transaction> purchases;
```

---

## Troubleshooting

### Connection Issues
```bash
# Check MySQL container status
docker ps | grep mysql

# Check MySQL logs
docker logs gameaccount-mysql

# Test connection
docker exec -it gameaccount-mysql mysql -u appuser -papppassword gameaccount_marketplace
```

### Schema Not Updating
- Verify `spring.jpa.hibernate.ddl-auto: update`
- Check for SQL constraint violations in logs
- Drop and recreate database for clean schema

### Slow Queries
- Enable `spring.jpa.show-sql: true`
- Add missing indexes
- Use `@Query` with `JOIN FETCH` to prevent N+1 queries

---

## Related Files

| File | Description |
|------|-------------|
| `backend-java/src/main/resources/application.yml` | Database configuration |
| `docker-compose.yml` | Container setup |
| `backend-java/src/main/java/com/gameaccount/marketplace/config/JpaConfig.java` | JPA auditing config |
| `backend-java/src/main/resources/seed_data.sql` | SQL test data |
| `backend-java/src/main/java/com/gameaccount/marketplace/util/DatabaseSeeder.java` | Java seeder |

---

*Last Updated: 2025-01-09*
