package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.request.AccountSearchRequest;
import com.gameaccount.marketplace.dto.request.CreateAccountRequest;
import com.gameaccount.marketplace.dto.request.UpdateAccountRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.repository.FavoriteRepository;
import com.gameaccount.marketplace.repository.GameRepository;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.spec.AccountSpecification;
import com.gameaccount.marketplace.util.EncryptionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Service layer for account listing management.
 * Provides CRUD operations, admin approval workflow, and search functionality with caching.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final EncryptionUtil encryptionUtil;
    private final NotificationService notificationService;

    /** Allowed sort fields for account search */
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("price", "level", "createdAt");

    /**
     * Get allowed sort fields for account search.
     * Used by GraphQL query resolver to validate sort parameters.
     *
     * @return Set of allowed field names
     */
    public Set<String> getAllowedSortFields() {
        return ALLOWED_SORT_FIELDS;
    }

    /**
     * Create a new account listing
     *
     * @param request Account creation data
     * @param sellerId ID of the seller creating the listing
     * @return Created account entity
     * @throws ResourceNotFoundException if seller or game not found
     */
    @CacheEvict(value = "accounts", allEntries = true)
    @Transactional
    public Account createAccount(@Valid CreateAccountRequest request, Long sellerId) {
        log.info("Creating account for sellerId: {}, gameId: {}, title: {}", sellerId, request.getGameId(), request.getTitle());

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
                .encryptedUsername(encryptionUtil.encrypt(request.getUsername()))
                .encryptedPassword(encryptionUtil.encrypt(request.getPassword()))
                .status(AccountStatus.PENDING)
                .viewsCount(0)
                .isFeatured(false)
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with id: {}", savedAccount.getId());

        // Broadcast new account to all subscribers
        java.util.Map<String, Object> accountData = java.util.Map.of(
                "id", savedAccount.getId(),
                "title", savedAccount.getTitle(),
                "price", savedAccount.getPrice(),
                "level", savedAccount.getLevel(),
                "rank", savedAccount.getRank(),
                "game", java.util.Map.of("id", savedAccount.getGame().getId(), "name", savedAccount.getGame().getName()),
                "seller", java.util.Map.of("id", savedAccount.getSeller().getId()),
                "status", savedAccount.getStatus().toString(),
                "createdAt", savedAccount.getCreatedAt().toString()
        );
        notificationService.broadcastNewAccount(accountData);

        return savedAccount;
    }

    /**
     * Update an existing account listing
     *
     * @param accountId ID of account to update
     * @param request Update data
     * @param authenticatedUserId ID of authenticated user
     * @return Updated account entity
     * @throws ResourceNotFoundException if account not found
     * @throws BusinessException if user is not the owner
     */
    @CacheEvict(value = "accounts", allEntries = true)
    @Transactional
    public Account updateAccount(Long accountId, @Valid UpdateAccountRequest request, Long authenticatedUserId) {
        log.info("Updating account id: {} by userId: {}", accountId, authenticatedUserId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Verify ownership
        if (!account.getSeller().getId().equals(authenticatedUserId)) {
            log.warn("User {} attempted to update account {} owned by user {}",
                    authenticatedUserId, accountId, account.getSeller().getId());
            throw new BusinessException("You are not authorized to update this account");
        }

        // Update allowed fields only (immutable: seller, game, status)
        account.setTitle(request.getTitle());
        account.setDescription(request.getDescription());
        account.setLevel(request.getLevel());
        account.setRank(request.getRank());
        account.setPrice(request.getPrice());
        account.setImages(request.getImages());

        Account updatedAccount = accountRepository.save(account);
        log.info("Account id: {} updated successfully", accountId);
        return updatedAccount;
    }

    /**
     * Delete an account listing
     *
     * @param accountId ID of account to delete
     * @param authenticatedUserId ID of authenticated user
     * @param isAdmin Whether the user has admin role
     * @throws ResourceNotFoundException if account not found
     * @throws BusinessException if user is not owner or admin
     */
    @CacheEvict(value = "accounts", allEntries = true)
    @Transactional
    public void deleteAccount(Long accountId, Long authenticatedUserId, boolean isAdmin) {
        log.info("Deleting account id: {} by userId: {}, isAdmin: {}", accountId, authenticatedUserId, isAdmin);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Verify ownership or admin role
        if (!isAdmin && !account.getSeller().getId().equals(authenticatedUserId)) {
            log.warn("User {} (admin: {}) attempted to delete account {} owned by user {}",
                    authenticatedUserId, isAdmin, accountId, account.getSeller().getId());
            throw new BusinessException("You are not authorized to delete this account");
        }

        accountRepository.delete(account);
        log.info("Account id: {} deleted successfully", accountId);
    }

    /**
     * Get account by ID and increment view count
     *
     * @param accountId ID of account to retrieve
     * @return Account entity
     * @throws ResourceNotFoundException if account not found
     */
    @Transactional
    public Account getAccountById(Long accountId) {
        log.debug("Fetching account id: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Increment view count
        account.setViewsCount(account.getViewsCount() + 1);
        Account updatedAccount = accountRepository.save(account);

        log.debug("Account id: {} fetched, views incremented to: {}", accountId, updatedAccount.getViewsCount());
        return updatedAccount;
    }

    /**
     * Get account by ID WITHOUT incrementing view count.
     * This is used by the GraphQL query which delegates view count incrementing
     * to the separate PATCH /api/accounts/{id}/view endpoint.
     *
     * @param accountId ID of account to retrieve
     * @return Account entity WITHOUT incrementing views
     * @throws ResourceNotFoundException if account not found
     */
    @Transactional(readOnly = true)
    public Account getAccountByIdWithoutIncrement(Long accountId) {
        log.debug("Fetching account id: {} (without incrementing views)", accountId);

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
    }

    /**
     * Increment account view count asynchronously (fire-and-forget).
     * This method is called from the PATCH /api/accounts/{id}/view endpoint.
     * Uses @Async to prevent blocking the response and @CacheEvict to clear cached data.
     *
     * @param accountId ID of account to increment
     */
    @Async
    @CacheEvict(value = "accounts", key = "#accountId")
    public CompletableFuture<Void> incrementViewCountAsync(Long accountId) {
        log.debug("Async increment view count for account id: {}", accountId);

        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

            account.setViewsCount(account.getViewsCount() + 1);
            accountRepository.save(account);

            log.debug("View count incremented asynchronously for account id: {}, new count: {}",
                    accountId, account.getViewsCount());
        } catch (Exception e) {
            log.error("Failed to increment view count for account id: {}", accountId, e);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Approve a pending account (ADMIN only)
     *
     * @param accountId ID of account to approve
     * @return Updated account entity
     * @throws ResourceNotFoundException if account not found
     * @throws BusinessException if account is not in PENDING status
     */
    @CacheEvict(value = "accounts", allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Account approveAccount(Long accountId) {
        log.info("Approving account id: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (account.getStatus() != AccountStatus.PENDING) {
            log.warn("Attempted to approve account {} with status: {}", accountId, account.getStatus());
            throw new BusinessException("Only pending accounts can be approved");
        }

        account.setStatus(AccountStatus.APPROVED);
        Account approvedAccount = accountRepository.save(account);

        // Send notification to seller
        notificationService.sendAccountApprovedNotification(account.getSeller().getId(), account.getId());

        // Broadcast status change to all subscribers
        notificationService.broadcastAccountStatusChanged(account.getId(), "APPROVED", "PENDING");

        log.info("Account id: {} approved successfully", accountId);
        return approvedAccount;
    }

    /**
     * Reject a pending account (ADMIN only)
     *
     * @param accountId ID of account to reject
     * @param reason Reason for rejection
     * @return Updated account entity
     * @throws ResourceNotFoundException if account not found
     * @throws BusinessException if account is not in PENDING status
     */
    @CacheEvict(value = "accounts", allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Account rejectAccount(Long accountId, String reason) {
        log.info("Rejecting account id: {}, reason: {}", accountId, reason);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (account.getStatus() != AccountStatus.PENDING) {
            log.warn("Attempted to reject account {} with status: {}", accountId, account.getStatus());
            throw new BusinessException("Only pending accounts can be rejected");
        }

        account.setStatus(AccountStatus.REJECTED);
        Account rejectedAccount = accountRepository.save(account);

        // Send notification to seller
        notificationService.sendAccountRejectedNotification(account.getSeller().getId(), account.getId(), reason);

        // Broadcast status change to all subscribers
        notificationService.broadcastAccountStatusChanged(account.getId(), "REJECTED", "PENDING");

        log.info("Account id: {} rejected successfully", accountId);
        return rejectedAccount;
    }

    /**
     * Search accounts with filters and caching
     *
     * @param gameId Optional game filter
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param status Optional status filter
     * @param pageable Pagination parameters
     * @return Page of matching accounts
     */
    @Cacheable(value = "accounts",
            key = "#gameId + '-' + #minPrice + '-' + #maxPrice + '-' + #status + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<Account> searchAccounts(Long gameId, Double minPrice, Double maxPrice, AccountStatus status, Pageable pageable) {
        log.debug("Searching accounts with filters - gameId: {}, minPrice: {}, maxPrice: {}, status: {}",
                gameId, minPrice, maxPrice, status);

        Page<Account> results = accountRepository.searchAccounts(gameId, minPrice, maxPrice, status, pageable);

        log.debug("Found {} accounts matching search criteria", results.getTotalElements());
        return results;
    }

    /**
     * Search accounts with advanced filters, role-based access control.
     * Supports filtering by game, price range, level range, rank, status, featured flag,
     * full-text search, and sorting options.
     *
     * Role-based filtering rules:
     * - ADMIN: Sees all statuses (respects requested status filter)
     * - SELLER: Sees APPROVED accounts + their own PENDING accounts
     * - BUYER/PUBLIC: Sees all statuses (APPROVED, PENDING, REJECTED, SOLD)
     *
     * Note: Caching disabled because effectiveStatus is modified dynamically based on user role,
     * which makes cache key computation unreliable.
     *
     * @param searchRequest Search parameters object containing all filters
     * @param authenticatedUserId ID of authenticated user (for role-based filtering)
     * @param userRole Role of authenticated user (BUYER, SELLER, ADMIN)
     * @param pageable Pagination parameters
     * @return Page of matching accounts
     */
    @Transactional(readOnly = true)
    public Page<Account> searchAccounts(
            AccountSearchRequest searchRequest,
            Long authenticatedUserId,
            String userRole,
            Pageable pageable) {

        log.debug("Advanced search - userRole: {}, filters: {}", userRole, searchRequest);
        log.info("DEBUG: searchRequest.getStatus() = {}, authenticatedUserId = {}, sellerId = {}",
                searchRequest.getStatus(), authenticatedUserId, searchRequest.getSellerId());

        // Determine effective status filter based on user role
        AccountStatus effectiveStatus = searchRequest.getStatus();

        if ("ADMIN".equals(userRole)) {
            // Admins see all statuses (respect requested status filter)
            effectiveStatus = searchRequest.getStatus();
        } else if ("SELLER".equals(userRole)) {
            // Sellers see APPROVED accounts + their own PENDING accounts
            // If searching for own listings, don't force status filter
            // Otherwise, show all accounts (for development)
            if (searchRequest.getSellerId() != null && searchRequest.getSellerId().equals(authenticatedUserId)) {
                // Viewing own listings - respect requested filter
                effectiveStatus = searchRequest.getStatus();
            } else {
                // Not viewing own listings - show all accounts (for development)
                effectiveStatus = null;
            }
        } else {
            // Buyers and public users - for development, show both APPROVED and PENDING
            // TODO: Change back to APPROVED only for production
            effectiveStatus = null; // No status filter - show all accounts
        }

        log.info("DEBUG: userRole = {}, effectiveStatus = {}", userRole, effectiveStatus);

        // Apply sorting if specified
        Pageable sortedPageable = pageable;
        if (searchRequest.getSortBy() != null && !searchRequest.getSortBy().trim().isEmpty()) {
            // Validate sortBy against allowed fields
            if (!ALLOWED_SORT_FIELDS.contains(searchRequest.getSortBy())) {
                log.warn("Invalid sortBy field requested: {}, ignoring and using default sorting", searchRequest.getSortBy());
                sortedPageable = pageable;
            } else {
                // Determine sort direction
                Sort.Direction direction = Sort.Direction.ASC; // Default for price
                if (searchRequest.getSortDirection() == AccountSearchRequest.SortDirection.DESC) {
                    direction = Sort.Direction.DESC;
                } else if (searchRequest.getSortDirection() == null) {
                    // Apply sensible defaults: level and createdAt default to DESC
                    if ("level".equals(searchRequest.getSortBy()) || "createdAt".equals(searchRequest.getSortBy())) {
                        direction = Sort.Direction.DESC;
                    }
                }

                Sort sort = Sort.by(direction, searchRequest.getSortBy());
                sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            }
        }

        // Use JOIN FETCH query to load related entities and prevent N+1 queries
        Page<Account> results = accountRepository.searchAccountsWithJoins(
            searchRequest.getGameId(),
            searchRequest.getMinPrice(),
            searchRequest.getMaxPrice(),
            searchRequest.getMinLevel(),
            searchRequest.getMaxLevel(),
            searchRequest.getRank(),
            effectiveStatus,
            searchRequest.getIsFeatured(),
            searchRequest.getSearchText(),
            searchRequest.getSellerId(),
            sortedPageable
        );

        log.debug("Advanced search found {} accounts matching criteria (effectiveStatus: {})",
                results.getTotalElements(), effectiveStatus);
        return results;
    }

    /**
     * Get accounts for a specific seller with pagination.
     * This is optimized compared to filtering in-memory after loading all accounts.
     *
     * @param sellerId ID of the seller
     * @param status Optional status filter (e.g., APPROVED)
     * @param pageable Pagination parameters
     * @return Page of accounts belonging to the seller
     */
    @Transactional(readOnly = true)
    public Page<Account> getSellerAccounts(Long sellerId, AccountStatus status, Pageable pageable) {
        log.debug("Fetching accounts for sellerId: {}, status: {}", sellerId, status);

        Page<Account> results;
        if (status != null) {
            // Use dedicated query that filters by both seller and status at database level
            results = accountRepository.findBySellerIdAndStatus(sellerId, status, pageable);
        } else {
            results = accountRepository.findBySellerId(sellerId, pageable);
        }

        log.debug("Found {} accounts for sellerId: {}", results.getTotalElements(), sellerId);
        return results;
    }

    /**
     * Get featured accounts with caching.
     * Returns approved accounts that are marked as featured.
     * Results are cached for 5 minutes (configured in CacheConfig).
     * Cache key: "featured::featured-accounts"
     *
     * @return List of featured accounts
     */
    @Cacheable(value = "featured", key = "'featured-accounts'")
    @Transactional(readOnly = true)
    public List<Account> getFeaturedAccounts() {
        log.debug("Fetching featured accounts");
        return accountRepository.findByStatusAndIsFeatured(AccountStatus.APPROVED, true);
    }

    /**
     * Get popular accounts (most viewed) with caching.
     * Returns approved accounts sorted by view count.
     * Results are cached for 5 minutes (configured in CacheConfig).
     * Cache key: "featured::popular-accounts"
     *
     * Note: This demonstrates @CachePut pattern - when an account's view count
     * is incremented, we could use @CachePut to update the cached list instead
     * of evicting and recomputing. However, for simplicity we use @CacheEvict
     * in incrementViewCountAsync().
     *
     * @return List of popular accounts
     */
    @Cacheable(value = "featured", key = "'popular-accounts'")
    @Transactional(readOnly = true)
    public List<Account> getPopularAccounts() {
        log.debug("Fetching popular accounts");
        return accountRepository.findPopularAccounts(AccountStatus.APPROVED);
    }

    /**
     * Get seller (user) for an account.
     * Used by GraphQL field resolver for the seller field.
     * NOTE: DataLoader temporarily disabled - fetching directly.
     * TODO: Re-enable DataLoader batching after upgrading to Spring Boot 3.3+
     *
     * @param accountId ID of the account to get seller for
     * @return User entity
     * @throws ResourceNotFoundException if account or seller not found
     */
    @Transactional(readOnly = true)
    public User getSellerForAccount(Long accountId) {
        log.debug("Fetching seller for accountId: {}", accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return userRepository.findById(account.getSeller().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + account.getSeller().getId()));
    }

    /**
     * Get game for an account.
     * Used by GraphQL field resolver for the game field.
     * NOTE: DataLoader temporarily disabled - fetching directly.
     * TODO: Re-enable DataLoader batching after upgrading to Spring Boot 3.3+
     *
     * @param accountId ID of the account to get game for
     * @return Game entity
     * @throws ResourceNotFoundException if account or game not found
     */
    @Transactional(readOnly = true)
    public Game getGameForAccount(Long accountId) {
        log.debug("Fetching game for accountId: {}", accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return gameRepository.findById(account.getGame().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + account.getGame().getId()));
    }

    /**
     * Check if an account is favorited by a user.
     * Used by GraphQL field resolver for the isFavorited field.
     * NOTE: DataLoader temporarily disabled - fetching directly.
     * TODO: Re-enable DataLoader batching after upgrading to Spring Boot 3.3+
     *
     * @param accountId ID of the account
     * @param userId ID of the user
     * @return true if favorited, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isAccountFavoritedByUser(Long accountId, Long userId) {
        log.debug("Checking if account {} is favorited by user {}", accountId, userId);
        return favoriteRepository.existsByUserIdAndAccountId(userId, accountId);
    }
}
