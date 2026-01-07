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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Create a new account listing
     *
     * @param request Account creation data
     * @param sellerId ID of the seller creating the listing
     * @return Created account entity
     * @throws ResourceNotFoundException if seller or game not found
     */
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
                .status(AccountStatus.PENDING)
                .viewsCount(0)
                .isFeatured(false)
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with id: {}", savedAccount.getId());
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
     * Approve a pending account (ADMIN only)
     *
     * @param accountId ID of account to approve
     * @return Updated account entity
     * @throws ResourceNotFoundException if account not found
     * @throws BusinessException if account is not in PENDING status
     */
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
        // Note: rejectionReason could be added as a field to Account entity in the future
        // For now, the reason is logged for audit purposes
        Account rejectedAccount = accountRepository.save(account);

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
}
