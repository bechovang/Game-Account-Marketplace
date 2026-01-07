# Story 2.2: AccountService Business Logic

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a developer,
I want to implement AccountService with CRUD and approval logic,
so that sellers can manage their listings and admins can approve them.

## Acceptance Criteria

1. **Given** the repositories from Story 2.1
**When** I implement AccountService
**Then** AccountService is annotated with @Service and @RequiredArgsConstructor
**And** createAccount() method validates seller exists via UserRepository
**And** createAccount() method validates game exists via GameRepository
**And** createAccount() method sets status to PENDING
**And** createAccount() method sets seller and game from repositories
**And** createAccount() method is annotated with @Transactional
**And** updateAccount() method verifies account belongs to authenticated seller
**And** updateAccount() method updates allowed fields only
**And** deleteAccount() method verifies ownership or admin role
**And** deleteAccount() method is annotated with @Transactional
**And** approveAccount() method changes status to APPROVED
**And** approveAccount() method is restricted to ADMIN role via @PreAuthorize
**And** rejectAccount() method changes status to REJECTED with reason
**And** searchAccounts() method supports filtering by gameId, minPrice, maxPrice, status
**And** searchAccounts() method is annotated with @Cacheable for Redis caching
**And** getAccountById() method increments viewsCount
**And** exceptions include: ResourceNotFoundException, BusinessException (not owner, invalid status transition)

## Tasks / Subtasks

- [x] Create AccountService class (AC: #, #)
  - [x] Add @Service annotation
  - [x] Add @RequiredArgsConstructor for dependency injection
  - [x] Inject AccountRepository, GameRepository, UserRepository
  - [x] Add class-level JavaDoc for service documentation
- [x] Implement createAccount() method (AC: #, #, #, #, #, #)
  - [x] Validate seller exists via UserRepository.findById()
  - [x] Throw ResourceNotFoundException if seller not found
  - [x] Validate game exists via GameRepository.findById()
  - [x] Throw ResourceNotFoundException if game not found
  - [x] Build Account entity with AccountStatus.PENDING
  - [x] Set seller and game from repository lookups
  - [x] Save account via AccountRepository.save()
  - [x] Add @Transactional annotation for ACID compliance
  - [x] Return saved Account entity
- [x] Implement updateAccount() method (AC: #, #)
  - [x] Accept accountId, UpdateAccountRequest, authenticatedUserId
  - [x] Fetch account via AccountRepository.findById()
  - [x] Throw ResourceNotFoundException if account not found
  - [x] Verify account.seller.id == authenticatedUserId (ownership check)
  - [x] Throw BusinessException with "Not authorized to update this account" if not owner
  - [x] Update only allowed fields: title, description, level, rank, price, images
  - [x] Do NOT allow status changes (admin only)
  - [x] Do NOT allow seller/game changes (immutable)
  - [x] Save updated account via AccountRepository.save()
  - [x] Return updated Account entity
- [x] Implement deleteAccount() method (AC: #, #)
  - [x] Accept accountId, authenticatedUserId, userRole
  - [x] Fetch account via AccountRepository.findById()
  - [x] Throw ResourceNotFoundException if account not found
  - [x] Verify ownership OR admin role
  - [x] If not admin, check account.seller.id == authenticatedUserId
  - [x] Throw BusinessException with "Not authorized to delete this account" if not authorized
  - [x] Add @Transactional annotation
  - [x] Delete account via AccountRepository.delete()
- [x] Implement getAccountById() method (AC: #)
  - [x] Accept accountId parameter
  - [x] Fetch account via AccountRepository.findById()
  - [x] Throw ResourceNotFoundException if account not found
  - [x] Increment viewsCount field
  - [x] Save account to persist viewsCount increment
  - [x] Return Account entity
- [x] Implement approveAccount() method (AC: #, #)
  - [x] Accept accountId parameter
  - [x] Add @PreAuthorize("hasRole('ADMIN')") annotation
  - [x] Fetch account via AccountRepository.findById()
  - [x] Throw ResourceNotFoundException if account not found
  - [x] Validate current status is PENDING
  - [x] Throw BusinessException if status is not PENDING
  - [x] Update status to AccountStatus.APPROVED
  - [x] Save account via AccountRepository.save()
  - [x] Return updated Account entity
- [x] Implement rejectAccount() method (AC: #)
  - [x] Accept accountId and rejectionReason parameters
  - [x] Add @PreAuthorize("hasRole('ADMIN')") annotation
  - [x] Fetch account via AccountRepository.findById()
  - [x] Throw ResourceNotFoundException if account not found
  - [x] Validate current status is PENDING
  - [x] Throw BusinessException if status is not PENDING
  - [x] Update status to AccountStatus.REJECTED
  - [x] Store rejection reason (add field or use notes field)
  - [x] Save account via AccountRepository.save()
  - [x] Return updated Account entity
- [x] Implement searchAccounts() method (AC: #, #)
  - [x] Accept gameId (optional), minPrice (optional), maxPrice (optional), status (optional), pageable parameters
  - [x] Delegate to AccountRepository.searchAccounts() method
  - [x] Add @Cacheable annotation with key based on parameters
  - [x] Set cache TTL to 10 minutes (600 seconds)
  - [x] Return Page<Account> with results
- [x] Create DTO classes (AC: #)
  - [x] Create CreateAccountRequest DTO
  - [x] Create UpdateAccountRequest DTO
  - [x] Create AccountResponse DTO
  - [x] Add @Valid annotations for validation
  - [x] Add validation constraints (@NotNull, @NotBlank, @Min, @Max)
- [x] Create exception classes (AC: #)
  - [x] Create ResourceNotFoundException extends RuntimeException
  - [x] Create BusinessException extends RuntimeException
  - [x] Add @ResponseStatus annotations for HTTP mapping
  - [x] Add descriptive error messages
- [x] Write unit tests (AC: #)
  - [x] Test createAccount() with valid data
  - [x] Test createAccount() with non-existent seller
  - [x] Test createAccount() with non-existent game
  - [x] Test updateAccount() by owner
  - [x] Test updateAccount() by non-owner (should fail)
  - [x] Test deleteAccount() by owner
  - [x] Test deleteAccount() by admin
  - [x] Test approveAccount() by admin
  - [x] Test approveAccount() by non-admin (should fail)
  - [x] Test getAccountById() increments viewsCount
  - [x] Test searchAccounts() with various filters
  - [x] Mock repository dependencies
  - [x] Verify repository methods called correctly
- [x] Verify service compiles and integrates correctly
  - [x] Compile project with mvn clean compile
  - [x] Verify no Spring autowiring errors
  - [x] Verify transaction management configured
  - [x] Verify security annotations processed

## Dev Notes

**Important:** This is a business logic story - focus on service layer with proper validation, authorization, and transaction management. No controllers yet - just the service that will be shared by REST and GraphQL.

### Project Structure Alignment

**Backend Package Structure:** [Source: Story 1.2]
```
backend-java/src/main/java/com/gameaccount/marketplace/
├── service/
│   ├── AccountService.java (CREATE)
│   └── AuthService.java (existing - reference for patterns)
├── dto/
│   ├── request/
│   │   ├── CreateAccountRequest.java (CREATE)
│   │   └── UpdateAccountRequest.java (CREATE)
│   └── response/
│       └── AccountResponse.java (CREATE)
├── exception/
│   ├── ResourceNotFoundException.java (CREATE if not exists)
│   └── BusinessException.java (CREATE if not exists)
```

### Service Layer Pattern Reference

**CRITICAL:** Follow Spring Service best practices:

```java
package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.request.CreateAccountRequest;
import com.gameaccount.marketplace.dto.request.UpdateAccountRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.repository.GameRepository;
import com.gameaccount.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    /**
     * Create a new account listing
     * @param request Account creation data
     * @param sellerId ID of the seller creating the listing
     * @return Created account entity
     * @throws ResourceNotFoundException if seller or game not found
     */
    @Transactional
    public Account createAccount(CreateAccountRequest request, Long sellerId) {
        // Validate seller exists
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + sellerId));

        // Validate game exists
        Game game = gameRepository.findById(request.getGameId())
            .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + request.getGameId()));

        // Build account entity
        Account account = Account.builder()
            .seller(seller)
            .game(game)
            .title(request.getTitle())
            .description(request.getDescription())
            .level(request.getLevel())
            .rank(request.getRank())
            .price(request.getPrice())
            .images(request.getImages())
            .status(AccountStatus.PENDING)
            .viewsCount(0)
            .isFeatured(false)
            .build();

        return accountRepository.save(account);
    }

    /**
     * Update an existing account listing
     * @param accountId ID of account to update
     * @param request Update data
     * @param authenticatedUserId ID of authenticated user
     * @return Updated account entity
     * @throws ResourceNotFoundException if account not found
     * @throws BusinessException if user is not the owner
     */
    public Account updateAccount(Long accountId, UpdateAccountRequest request, Long authenticatedUserId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Verify ownership
        if (!account.getSeller().getId().equals(authenticatedUserId)) {
            throw new BusinessException("You are not authorized to update this account");
        }

        // Update allowed fields only
        account.setTitle(request.getTitle());
        account.setDescription(request.getDescription());
        account.setLevel(request.getLevel());
        account.setRank(request.getRank());
        account.setPrice(request.getPrice());
        account.setImages(request.getImages());

        return accountRepository.save(account);
    }

    /**
     * Delete an account listing
     * @param accountId ID of account to delete
     * @param authenticatedUserId ID of authenticated user
     * @param isAdmin Whether the user has admin role
     * @throws ResourceNotFoundException if account not found
     * @throws BusinessException if user is not owner or admin
     */
    @Transactional
    public void deleteAccount(Long accountId, Long authenticatedUserId, boolean isAdmin) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Verify ownership or admin role
        if (!isAdmin && !account.getSeller().getId().equals(authenticatedUserId)) {
            throw new BusinessException("You are not authorized to delete this account");
        }

        accountRepository.delete(account);
    }

    /**
     * Get account by ID and increment view count
     * @param accountId ID of account to retrieve
     * @return Account entity
     * @throws ResourceNotFoundException if account not found
     */
    public Account getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Increment view count
        account.setViewsCount(account.getViewsCount() + 1);
        return accountRepository.save(account);
    }

    /**
     * Approve a pending account (ADMIN only)
     * @param accountId ID of account to approve
     * @return Updated account entity
     * @throws ResourceNotFoundException if account not found
     * @throws BusinessException if account is not in PENDING status
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Account approveAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (account.getStatus() != AccountStatus.PENDING) {
            throw new BusinessException("Only pending accounts can be approved");
        }

        account.setStatus(AccountStatus.APPROVED);
        return accountRepository.save(account);
    }

    /**
     * Reject a pending account (ADMIN only)
     * @param accountId ID of account to reject
     * @param reason Reason for rejection
     * @return Updated account entity
     * @throws ResourceNotFoundException if account not found
     * @throws BusinessException if account is not in PENDING status
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Account rejectAccount(Long accountId, String reason) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (account.getStatus() != AccountStatus.PENDING) {
            throw new BusinessException("Only pending accounts can be rejected");
        }

        account.setStatus(AccountStatus.REJECTED);
        // Note: Add rejectionReason field if needed, or store in description/notes
        return accountRepository.save(account);
    }

    /**
     * Search accounts with filters
     * @param gameId Optional game filter
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param status Optional status filter
     * @param pageable Pagination parameters
     * @return Page of matching accounts
     */
    @Cacheable(value = "accounts", key = "#gameId + '-' + #minPrice + '-' + #maxPrice + '-' + #status + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Account> searchAccounts(Long gameId, Double minPrice, Double maxPrice, AccountStatus status, Pageable pageable) {
        return accountRepository.searchAccounts(gameId, minPrice, maxPrice, status, pageable);
    }
}
```

### DTO Patterns

**CreateAccountRequest.java:**
```java
package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateAccountRequest {

    @NotNull(message = "Game ID is required")
    private Long gameId;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private Integer level;

    @Size(max = 50, message = "Rank must not exceed 50 characters")
    private String rank;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    @Size(max = 10, message = "Maximum 10 images allowed")
    private List<@NotBlank String> images;
}
```

**UpdateAccountRequest.java:**
```java
package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UpdateAccountRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private Integer level;

    @Size(max = 50, message = "Rank must not exceed 50 characters")
    private String rank;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    @Size(max = 10, message = "Maximum 10 images allowed")
    private List<@NotBlank String> images;
}
```

**AccountResponse.java:**
```java
package com.gameaccount.marketplace.dto.response;

import com.gameaccount.marketplace.entity.Account.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private Long sellerId;
    private String sellerName;
    private Long gameId;
    private String gameName;
    private String title;
    private String description;
    private Integer level;
    private String rank;
    private Double price;
    private AccountStatus status;
    private Integer viewsCount;
    private Boolean isFeatured;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Exception Classes

**ResourceNotFoundException.java:**
```java
package com.gameaccount.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

**BusinessException.java:**
```java
package com.gameaccount.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

### Common Pitfalls to Avoid

🚨 **CRITICAL WARNINGS:**
1. **Missing @Transactional** - Without @Transactional, database changes won't be atomic and won't rollback on errors
2. **Forgetting ownership checks** - Always verify the user owns the resource before allowing updates/deletes
3. **Allowing immutable field changes** - Never allow changes to seller, game, or status in updateAccount()
4. **Missing validation** - Always validate that referenced entities (seller, game) exist before creating accounts
5. **Wrong cache keys** - Cache keys must include all filter parameters or wrong data will be returned
6. **N+1 query problem** - Be careful when accessing account.seller or account.game - they may trigger additional queries
7. **ViewsCount race condition** - Incrementing viewsCount in getAccountById() could have race conditions under high load
8. **Missing @PreAuthorize** - Admin methods MUST have @PreAuthorize annotation for security
9. **Transaction boundaries** - Keep transactions as short as possible - don't include external API calls
10. **Not handling enums correctly** - Use AccountStatus.PENDING not the string "PENDING"

### Testing Standards

```java
@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "seller@example.com", roles = {"SELLER"})
    void createAccount_ValidData_ReturnsAccount() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest();
        request.setGameId(1L);
        request.setTitle("Test Account");
        request.setPrice(100.0);

        User seller = User.builder().id(1L).email("seller@example.com").build();
        Game game = Game.builder().id(1L).name("Test Game").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Account result = accountService.createAccount(request, 1L);

        // Then
        assertNotNull(result);
        assertEquals(AccountStatus.PENDING, result.getStatus());
        assertEquals(seller, result.getSeller());
        assertEquals(game, result.getGame());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_NonExistentSeller_ThrowsException() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest();
        request.setGameId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.createAccount(request, 1L);
        });
    }

    @Test
    void updateAccount_NotOwner_ThrowsException() {
        // Given
        Long authenticatedUserId = 999L; // Different from seller
        UpdateAccountRequest request = new UpdateAccountRequest();
        request.setTitle("Updated Title");

        Account existingAccount = Account.builder()
            .id(1L)
            .seller(User.builder().id(1L).build())
            .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));

        // When/Then
        assertThrows(BusinessException.class, () -> {
            accountService.updateAccount(1L, request, authenticatedUserId);
        });
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void approveAccount_ValidAccount_ChangesStatus() {
        // Given
        Account account = Account.builder()
            .id(1L)
            .status(AccountStatus.PENDING)
            .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Account result = accountService.approveAccount(1L);

        // Then
        assertEquals(AccountStatus.APPROVED, result.getStatus());
        verify(accountRepository).save(account);
    }

    @Test
    void getAccountById_ValidAccount_IncrementsViewsCount() {
        // Given
        Account account = Account.builder()
            .id(1L)
            .viewsCount(100)
            .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Account result = accountService.getAccountById(1L);

        // Then
        assertEquals(101, result.getViewsCount());
    }
}
```

### Redis Caching Configuration

**application.yml additions:**
```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutes in milliseconds
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```

**Enable Caching:**
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```

### Requirements Traceability

**FR10:** Create account listing ✅ createAccount() method
**FR11:** Edit account listing ✅ updateAccount() method
**FR12:** Delete account listing ✅ deleteAccount() method
**FR35:** Approve accounts ✅ approveAccount() method with @PreAuthorize
**FR36:** Reject accounts ✅ rejectAccount() method with reason
**FR37:** View account details ✅ getAccountById() method
**NFR41:** ACID transactions ✅ @Transactional annotations
**NFR45:** Redis caching ✅ @Cacheable annotations
**NFR46:** Indexing for search ✅ Delegates to AccountRepository.searchAccounts()

### Next Story Dependencies

Story 2.3 (GraphQL Schema & Resolvers) - Depends on AccountService being complete
Story 2.4 (REST Controllers) - Depends on AccountService being complete

### Previous Story Intelligence (Story 2.1)

**Key Learnings from Story 2.1 (Game & Account Entities):**
- Account entity has AccountStatus enum: PENDING, APPROVED, REJECTED, SOLD
- Account.seller uses LAZY fetch - be careful about lazy loading exceptions
- Account.game uses EAGER fetch - safe to access without transactions
- @Column(precision, scale) does NOT work with Double types (learned from code review)
- SQL reserved keywords like "rank" need @Column(name="player_rank")
- Spring Data JPA method names must match field names exactly (e.g., findByStatusAndIsFeatured)
- Redis auto-configuration must be excluded if not used (learned from code review)

**Key Files from Story 2.1:**
- `backend-java/src/main/java/com/gameaccount/marketplace/entity/Account.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/entity/Game.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/repository/AccountRepository.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/repository/GameRepository.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/repository/UserRepository.java`

### References

- Epics.md: Section Epic 2, Story 2.2 (full requirements)
- Story 2.1: Account entity and repository patterns
- Spring Boot 3.2.1 Documentation: @Service, @Transactional, @Cacheable
- Spring Security Documentation: @PreAuthorize for method-level security
- JPA Documentation: Transaction management, lazy loading

---

## Dev Agent Record

### Agent Model Used

Claude Opus 4.5 (BMad Master)

### Debug Log References

None - Initial story creation

### Completion Notes List

Story 2.2 implementation completed on 2026-01-07.

**Implementation Summary:**
- Created AccountService with complete CRUD operations and admin approval workflow
- Added Redis caching configuration with 10-minute TTL for search results
- Created 3 DTO classes with full validation (CreateAccountRequest, UpdateAccountRequest, AccountResponse)
- Enhanced exception classes with @ResponseStatus annotations
- Created comprehensive unit tests (23 tests, all passing)
- Compilation successful: 26 source files compiled

**Technical Implementation Details:**
- AccountService: 6 business methods (create, update, delete, getById, approve, reject, search)
- Transaction management: @Transactional on createAccount(), updateAccount(), deleteAccount(), getAccountById(), searchAccounts() (readOnly)
- Security: @PreAuthorize("hasRole('ADMIN')") on approve/reject methods
- Caching: @Cacheable on searchAccounts() with composite key
- Logging: Slf4j logging throughout for debugging and audit trail
- Ownership verification: update/delete methods check user ownership
- Status validation: approve/reject only work on PENDING status

**Code Review Fixes Applied (2026-01-07):**
- Added @Valid to createAccount() and updateAccount() method parameters to trigger bean validation
- Added @Transactional to updateAccount() to prevent LazyInitializationException when accessing seller
- Added @Transactional to getAccountById() to ensure viewsCount increment is atomic
- Added @Transactional(readOnly = true) to searchAccounts() for performance and to prevent lazy loading issues
- Updated CacheConfig with @ConditionalOnBean/@ConditionalOnMissingBean for graceful fallback when Redis unavailable
- Added jakarta.validation.Valid import to AccountService

**Files Created:**
- AccountService.java (242 lines with full JavaDoc)
- CreateAccountRequest.java (36 lines with validation)
- UpdateAccountRequest.java (35 lines with validation)
- AccountResponse.java (35 lines with builder pattern)
- CacheConfig.java (62 lines with Redis + fallback configuration)
- AccountServiceTest.java (525 lines, 23 tests)

**Files Modified:**
- ResourceNotFoundException.java (added @ResponseStatus(NOT_FOUND))
- BusinessException.java (added @ResponseStatus(FORBIDDEN))
- application.yml (enabled Redis caching with TTL configuration)

**Test Results:**
- 23 tests written and passing
- Test coverage includes:
  - CRUD operations (create, update, delete, get)
  - Admin operations (approve, reject)
  - Search with filters (game, price, status)
  - Error cases (not found, not authorized, invalid status)
  - Ownership verification
  - Caching behavior documented

**All acceptance criteria met and verified.**

### File List

**Files Created:**
- `backend-java/src/main/java/com/gameaccount/marketplace/service/AccountService.java` (CREATE - 242 lines, includes code review fixes)
- `backend-java/src/main/java/com/gameaccount/marketplace/dto/request/CreateAccountRequest.java` (CREATE - 36 lines)
- `backend-java/src/main/java/com/gameaccount/marketplace/dto/request/UpdateAccountRequest.java` (CREATE - 35 lines)
- `backend-java/src/main/java/com/gameaccount/marketplace/dto/response/AccountResponse.java` (CREATE - 35 lines)
- `backend-java/src/main/java/com/gameaccount/marketplace/config/CacheConfig.java` (CREATE - 62 lines, includes code review fixes)
- `backend-java/src/test/java/com/gameaccount/marketplace/service/AccountServiceTest.java` (CREATE - 525 lines)

**Files Modified:**
- `backend-java/src/main/java/com/gameaccount/marketplace/exception/ResourceNotFoundException.java` (MODIFIED - added @ResponseStatus)
- `backend-java/src/main/java/com/gameaccount/marketplace/exception/BusinessException.java` (MODIFIED - added @ResponseStatus)
- `backend-java/src/main/resources/application.yml` (MODIFIED - enabled Redis caching)

**Dependencies (existing files used):**
- `backend-java/src/main/java/com/gameaccount/marketplace/entity/Account.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/entity/Game.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/entity/User.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/repository/AccountRepository.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/repository/GameRepository.java`
- `backend-java/src/main/java/com/gameaccount/marketplace/repository/UserRepository.java`
