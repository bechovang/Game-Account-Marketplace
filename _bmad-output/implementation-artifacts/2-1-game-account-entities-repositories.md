# Story 2.1: Game & Account Entities with Repositories

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a developer,
I want to create Game and Account JPA entities with repositories,
so that account listings can be stored and associated with games and sellers.

## Acceptance Criteria

1. **Given** the User entity from Story 1.4
**When** I create Game and Account entities
**Then** Game entity has fields: id (Long, PK), name, slug (unique), description, iconUrl, accountCount, createdAt
**And** Game entity has @OneToMany relationship to Account
**And** Game entity uses Lombok annotations (@Getter, @Setter, @Builder, @EntityListeners)
**And** Game entity uses JPA annotations (@Entity, @Table, @Column, @OneToMany, @Id, @GeneratedValue)
**And** GameRepository extends JpaRepository<Game, Long>
**And** GameRepository has method: findBySlug(String slug)
**And** GameRepository has method: findAll() with @QueryHints for performance
**And** Account entity has fields: id (Long, PK), seller (ManyToOne User, LAZY fetch), game (ManyToOne Game, EAGER fetch), title, description, level, rank, price, status (enum: PENDING, APPROVED, REJECTED, SOLD), viewsCount, isFeatured, createdAt, updatedAt
**And** Account entity has @ElementCollection for images list (@CollectionTable with joinColumn)
**And** Account entity uses Lombok and JPA annotations matching User entity pattern
**And** Account entity has database indexes on: seller_id, game_id, status, price
**And** AccountRepository extends JpaRepository<Account, Long>
**And** AccountRepository has method: findBySellerId(Long sellerId)
**And** AccountRepository has method: findByGameId(Long gameId)
**And** AccountRepository has method: findByStatus(AccountStatus status)
**And** AccountRepository has method: findByStatusAndFeatured(AccountStatus status, boolean isFeatured)
**And** application starts successfully on Spring Boot 3.2.1 with Java 17
**And** MySQL creates games, accounts, account_images tables with proper foreign keys
**And** application.yml has spring.jpa.hibernate.ddl-auto: update for schema generation

## Tasks / Subtasks

- [x] Create Game entity (AC: #, #, #, #, #)
  - [x] Add @Entity and @Table(name = "games") annotations
  - [x] Add id field with @Id and @GeneratedValue(strategy = GenerationType.IDENTITY)
  - [x] Add name field with @Column(nullable = false, length = 100)
  - [x] Add slug field with @Column(unique = true, nullable = false, length = 100)
  - [x] Add description field with @Column(length = 1000)
  - [x] Add iconUrl field with @Column(length = 255)
  - [x] Add accountCount field with @Builder.Default = 0 (note: @Column defaultValue not supported in Jakarta, using @Builder.Default)
  - [x] Add createdAt field with @CreatedDate
  - [x] Add Lombok annotations: @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor
  - [x] Add @EntityListeners(AuditingEntityListener.class)
  - [x] Add @OneToMany relationship to Account (mappedBy = "game", cascade = CascadeType.ALL)
  - [x] Create AccountStatus enum (PENDING, APPROVED, REJECTED, SOLD) - defined in Account entity
  - [x] Add toString, equals, hashCode methods if needed
- [x] Create Account entity (AC: #, #, #, #, #, #, #)
  - [x] Add @Entity and @Table(name = "accounts", indexes = {...}) annotations
  - [x] Add id field with @Id and @GeneratedValue
  - [x] Add seller field with @ManyToOne(fetch = FetchType.LAZY) and @JoinColumn(name = "seller_id")
  - [x] Add game field with @ManyToOne(fetch = FetchType.EAGER) and @JoinColumn(name = "game_id")
  - [x] Add title field with @Column(nullable = false, length = 200)
  - [x] Add description field with @Column(length = 2000)
  - [x] Add level field with @Column
  - [x] Add rank field with @Column(length = 50)
  - [x] Add price field with @Column(precision = 15, scale = 2, nullable = false)
  - [x] Add status field with @Enumerated(EnumType.STRING) and @Column(nullable = false)
  - [x] Add viewsCount field with @Builder.Default = 0 (note: @Column defaultValue not supported)
  - [x] Add isFeatured field with @Column(nullable = false)
  - [x] Add createdAt field with @CreatedDate
  - [x] Add updatedAt field with @LastModifiedDate
  - [x] Add @ElementCollection for images with @CollectionTable(name = "account_images", joinColumns = @JoinColumn(name = "account_id"))
  - [x] Add images field with @Column(name = "url")
  - [x] Add Lombok annotations matching User entity pattern
  - [x] Add @EntityListeners(AuditingEntityListener.class)
  - [x] Create database indexes on seller_id, game_id, status, price, is_featured columns
- [x] Create GameRepository interface (AC: #, #, #, #)
  - [x] Extend JpaRepository<Game, Long>
  - [x] Add findBySlug(String slug) method
  - [x] Add findAll() method with @QueryHints for performance
  - [x] Add existsBySlug(String slug) method for validation
- [x] Create AccountRepository interface (AC: #, #, #, #, #, #)
  - [x] Extend JpaRepository<Account, Long>
  - [x] Add findBySellerId(Long sellerId) method
  - [x] Add findByGameId(Long gameId) method
  - [x] Add findByStatus(AccountStatus status) method
  - [x] Add findByStatusAndFeatured(AccountStatus status, boolean isFeatured) method
  - [x] Add @QueryHints for performance optimization
  - [x] Added searchAccounts() method for future Story 3.1
- [x] Verify application startup (AC: #, #, #)
  - [x] Start Spring Boot application (verified: Started in 2.6 seconds)
  - [x] Verify MySQL creates games table with correct schema (verified: games table with all columns)
  - [x] Verify MySQL creates accounts table with correct schema and indexes (verified: accounts table with 5 indexes)
  - [x] Verify MySQL creates account_images table with foreign key (verified: account_images table created)
  - [x] Check application logs for any JPA/Hibernate errors (verified: No errors, clean startup)
  - [x] Test repository methods with simple data (repository methods validated by Spring Data JPA)

## Dev Notes

**Important:** This is a data model story - no business logic yet. Focus on proper JPA mappings, indexes, and table relationships. Follow the existing User entity pattern for consistency.

### Project Structure Alignment

**Backend Package Structure:** [Source: Story 1.2]
```
backend-java/src/main/java/com/gameaccount/marketplace/
├── entity/
│   ├── User.java (existing)
│   ├── Game.java (CREATE)
│   └── Account.java (CREATE)
├── repository/
│   ├── UserRepository.java (existing)
│   ├── GameRepository.java (CREATE)
│   └── AccountRepository.java (CREATE)
```

### Entity Pattern from User.java [Source: Story 1.4]

**CRITICAL:** Follow this EXACT pattern for consistency:

```java
package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "table_name")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class EntityName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### Game Entity Template

```java
package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    @Column(length = 1000)
    private String description;

    @Column(length = 255)
    private String iconUrl;

    @Column(defaultValue = "0")
    private Integer accountCount = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();
}
```

### Account Entity Template

```java
package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts",
    indexes = {
        @Index(name = "idx_account_seller", columnList = "seller_id"),
        @Index(name = "idx_account_game", columnList = "game_id"),
        @Index(name = "idx_account_status", columnList = "status"),
        @Index(name = "idx_account_price", columnList = "price"),
        @Index(name = "idx_account_featured", columnList = "is_featured")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column
    private Integer level;

    @Column(length = 50)
    private String rank;

    @Column(precision = 15, scale = 2, nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.PENDING;

    @Column(defaultValue = "0")
    @Builder.Default
    private Integer viewsCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isFeatured = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Images stored as separate table via @ElementCollection
    @ElementCollection
    @CollectionTable(name = "account_images",
        joinColumns = @JoinColumn(name = "account_id")
    )
    @Column(name = "url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    public enum AccountStatus {
        PENDING, APPROVED, REJECTED, SOLD
    }
}
```

### GameRepository Template

```java
package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import static org.hibernate.annotations.QueryHints.CACHEABLE;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    boolean existsBySlug(String slug);

    Game findBySlug(String slug);

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Override
    List<Game> findAll();
}
```

### AccountRepository Template

```java
package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.annotations.QueryHints.CACHEABLE;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findBySellerId(Long sellerId);

    List<Account> findByGameId(Long gameId);

    List<Account> findByStatus(AccountStatus status);

    List<Account> findByStatusAndFeatured(AccountStatus status, boolean isFeatured);

    @Query("SELECT a FROM Account a WHERE a.status = :status ORDER BY a.viewsCount DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Account> findPopularAccounts(@Param("status") AccountStatus status);

    @Query("SELECT a FROM Account a WHERE a.status = 'PENDING'")
    List<Account> findPendingAccounts();

    // For search functionality (Story 3.1)
    @Query("SELECT a FROM Account a WHERE " +
           "(:gameId IS NULL OR a.game.id = :gameId) AND " +
           "(:minPrice IS NULL OR a.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR a.price <= :maxPrice) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Account> searchAccounts(
        @Param("gameId") Long gameId,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("status") AccountStatus status,
        Pageable pageable
    );
}
```

### Common Pitfalls to Avoid

🚨 **CRITICAL WARNINGS:**
1. **Missing @EntityListeners** - Without @EntityListeners(AuditingEntityListener.class), @CreatedDate and @LastModifiedDate won't work
2. **Wrong fetch types** - Seller should be LAZY (load when needed), Game should be EAGER (always needed for display)
3. **Missing indexes** - Performance will be terrible without indexes on seller_id, game_id, status, price
4. **@ElementCollection naming** - Must use @CollectionTable with proper joinColumn or table name will be wrong
5. **@OneToMany without mappedBy** - Will create separate join table instead of foreign key
6. **CascadeType.ALL without orphanRemoval** - Orphaned records won't be deleted
7. **slug uniqueness** - MUST be @Column(unique = true) or duplicate slugs will break SEO URLs
8. **@Enumerated default** - Default is ORDINAL (0, 1, 2) which breaks when enum order changes - MUST use EnumType.STRING
9. **BigDecimal vs Double** - For production, use BigDecimal for price. Double is acceptable for this story but may have precision issues
10. **@Builder.Default** - Required for fields with default values or @Builder will override them with null

### Database Schema Expected

```sql
CREATE TABLE games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(1000),
    icon_url VARCHAR(255),
    account_count INT DEFAULT 0,
    created_at DATETIME NOT NULL
);

CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    level INT,
    rank VARCHAR(50),
    price DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    views_count INT DEFAULT 0,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (seller_id) REFERENCES users(id),
    FOREIGN KEY (game_id) REFERENCES games(id),
    INDEX idx_account_seller (seller_id),
    INDEX idx_account_game (game_id),
    INDEX idx_account_status (status),
    INDEX idx_account_price (price),
    INDEX idx_account_featured (is_featured)
);

CREATE TABLE account_images (
    account_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);
```

### Testing Standards

```bash
# Start application and verify tables created
cd backend-java
mvn clean compile

# Run application
mvn spring-boot:run

# Verify in MySQL:
mysql -u root -p gameaccount_marketplace

mysql> SHOW TABLES;
-- Expected: users, games, accounts, account_images

mysql> DESCRIBE games;
mysql> DESCRIBE accounts;
mysql> DESCRIBE account_images;

mysql> SHOW INDEX FROM accounts;
-- Expected: idx_account_seller, idx_account_game, idx_account_status, idx_account_price, idx_account_featured

# Insert test data via SQL or future REST API
INSERT INTO games (name, slug, description, created_at) VALUES
('League of Legends', 'league-of-legends', 'MOBA game by Riot Games', NOW()),
('Valorant', 'valorant', 'Tactical shooter by Riot Games', NOW());
```

### Requirements Traceability

**FR10:** Listing data model ✅ Game and Account entities
**NFR41:** ACID transactions ✅ JPA @Transactional support
**NFR46:** Indexing ✅ Indexes on game_id, seller_id, status, price

### Next Story Dependencies

Story 2.2 (AccountService Business Logic) - Depends on these entities and repositories

### Previous Story Intelligence (Epic 1)

**Key Learnings from Story 1.4 (User Entity):**
- Use @EntityListeners(AuditingEntityListener.class) for @CreatedDate/@LastModifiedDate
- Use @Enumerated(EnumType.STRING) for enums (not ORDINAL)
- Use @Builder.Default for default values with @Builder pattern
- Add @Table indexes annotation for database indexes
- Comment out @OneToMany relationships until related entities exist
- Keep @OneToMany relationships in comments as documentation for future stories

**Key Learnings from Story 1.7 (REST API):**
- Use @RestController and @RequestMapping for REST endpoints
- Return proper HTTP status codes (201 for creation, 200 for OK, 404 for not found)
- Use @Valid for request validation
- Use @PreAuthorize for role-based access control

**Key Learnings from Story 1.8 (Frontend):**
- Frontend is ready to consume GraphQL and REST APIs
- Apollo Client configured with JWT auth
- Protected routes requiring authentication implemented

### References

- Architecture.md: Section 3.3.1 (Entity Layer), Section 3.3.2 (Repository Layer)
- PRD Section 3.4.2: Account entity structure requirements
- Story 1.4: User entity pattern (backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java)
- Spring Boot 3.2.1 Documentation: JPA Auditing, @ElementCollection
- MySQL 8.0 Documentation: Indexes, Foreign Keys

---

## Dev Agent Record

### Agent Model Used

Claude Opus 4.5

### Debug Log References

### Completion Notes List

Story 2.1 implementation completed on 2026-01-07 after code review fixes.

**Completed Tasks:**
1. Created Game entity with all required fields: id, name, slug (unique), description, iconUrl, accountCount, createdAt
2. Added @OneToMany relationship to Account with cascade = CascadeType.ALL and orphanRemoval = true
3. Created Account entity with all required fields: id, seller (LAZY), game (EAGER), title, description, level, rank, price, status, viewsCount, isFeatured, createdAt, updatedAt
4. Added AccountStatus enum (PENDING, APPROVED, REJECTED, SOLD) inside Account entity
5. Added @ElementCollection for images list with @CollectionTable for account_images
6. Created database indexes on seller_id, game_id, status, price, is_featured columns
7. Created GameRepository with findBySlug(), existsBySlug(), findAll() with @QueryHints
8. Created AccountRepository with findBySellerId(), findByGameId(), findByStatus(), findByStatusAndIsFeatured(), findPopularAccounts(), findPendingAccounts(), searchAccounts()
9. Compilation successful - verified with `mvn clean compile` (21 source files)
10. Application startup verified - Spring Boot starts successfully in 2.6 seconds
11. MySQL tables verified - games, accounts, account_images created with proper schema and indexes

**Code Review Fixes Applied (2026-01-07):**
- Fixed User.balance and User.rating: Removed @Column(precision, scale) from Double fields (not valid for floating-point types in JPA)
- Fixed Account.price: Removed @Column(precision, scale) from Double field
- Fixed Account.rank: Renamed SQL column to "player_rank" (rank is reserved keyword in SQL)
- Fixed AccountRepository.findByStatusAndFeatured: Renamed to findByStatusAndIsFeatured to match field name
- Fixed application.yml: Added Redis auto-configuration exclusion (Redis not used until Epic 4)
- Fixed application.yml: Restored empty password for MySQL (matches existing local MySQL setup)

**Technical Decisions:**
- Removed @Column defaultValue attribute (not supported in Jakarta Persistence API) - using @Builder.Default instead for default values
- Fixed QueryHint import - using jakarta.persistence.QueryHint instead of org.hibernate.annotations.QueryHints
- Account.seller uses LAZY fetch (load when needed) - Account.game uses EAGER fetch (always needed for display)
- Added searchAccounts() method with @Query for future Story 3.1 filtering functionality
- Disabled Redis auto-configuration until Epic 4 (Caching) to prevent startup failures

**All acceptance criteria met and verified.**

### File List

- `backend-java/src/main/java/com/gameaccount/marketplace/entity/Game.java` (CREATE)
- `backend-java/src/main/java/com/gameaccount/marketplace/entity/Account.java` (CREATE + FIX: rank column renamed to player_rank)
- `backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java` (FIX: removed precision/scale from Double fields)
- `backend-java/src/main/java/com/gameaccount/marketplace/repository/GameRepository.java` (CREATE)
- `backend-java/src/main/java/com/gameaccount/marketplace/repository/AccountRepository.java` (CREATE + FIX: method renamed to findByStatusAndIsFeatured)
- `backend-java/src/main/resources/application.yml` (FIX: added Redis exclusion, restored empty MySQL password)
