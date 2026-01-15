package com.gameaccount.marketplace.controller;

import com.gameaccount.marketplace.dto.request.CreateAccountRequest;
import com.gameaccount.marketplace.dto.request.UpdateAccountRequest;
import com.gameaccount.marketplace.dto.response.AccountResponse;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Account management.
 * Provides CRUD operations for sellers to manage their account listings.
 * Follows shared service layer pattern - delegates all business logic to AccountService.
 */
@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Tag(name = "Account", description = "Account management APIs")
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    /**
     * Create a new account listing.
     * Requires authentication with SELLER or ADMIN role.
     * For multipart/form-data requests with file uploads.
     *
     * @param gameId    Game ID
     * @param title     Account title
     * @param description Account description
     * @param level     Account level
     * @param rank      Account rank
     * @param price     Account price
     * @param images    Image files (optional)
     * @return Created account response
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Create account", description = "Create a new account listing")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AccountResponse> createAccount(
            @Parameter(description = "Game ID", required = true)
            @RequestParam("gameId") Long gameId,

            @Parameter(description = "Account title", required = true)
            @RequestParam("title") String title,

            @Parameter(description = "Account description")
            @RequestParam(value = "description", required = false) String description,

            @Parameter(description = "Account level")
            @RequestParam(value = "level", required = false) Integer level,

            @Parameter(description = "Account rank")
            @RequestParam(value = "rank", required = false) String rank,

            @Parameter(description = "Account price", required = true)
            @RequestParam("price") Double price,

            @Parameter(description = "Account images")
            @RequestParam(value = "images", required = false) MultipartFile[] images
    ) {
        Long userId = getAuthenticatedUserId();
        log.info("POST /api/accounts - userId: {}, gameId: {}, title: {}", userId, gameId, title);

        // Handle image uploads
        List<String> imageUrls = handleImageUploads(images);

        // Build request
        CreateAccountRequest request = CreateAccountRequest.builder()
                .gameId(gameId)
                .title(title)
                .description(description)
                .level(level)
                .rank(rank)
                .price(price)
                .images(imageUrls)
                .build();

        // Delegate to service
        Account account = accountService.createAccount(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toAccountResponse(account));
    }

    /**
     * Create a new account listing (JSON format).
     * Alternative endpoint that accepts JSON instead of multipart/form-data.
     *
     * @param request Account creation data
     * @return Created account response
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Create account (JSON)", description = "Create a new account listing using JSON")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AccountResponse> createAccountJson(@Valid @RequestBody CreateAccountRequest request) {
        Long userId = getAuthenticatedUserId();
        log.info("POST /api/accounts (JSON) - userId: {}, gameId: {}, title: {}", userId, request.getGameId(), request.getTitle());

        // Delegate to service
        Account account = accountService.createAccount(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toAccountResponse(account));
    }

    /**
     * Update an existing account listing.
     * Requires authentication and ownership (seller) or admin role.
     *
     * @param id      Account ID
     * @param request Update data
     * @return Updated account response
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update account", description = "Update an existing account listing")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AccountResponse> updateAccount(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id,

            @Valid @RequestBody UpdateAccountRequest request
    ) {
        Long userId = getAuthenticatedUserId();
        log.info("PUT /api/accounts/{} - userId: {}", id, userId);

        // Delegate to service
        Account account = accountService.updateAccount(id, request, userId);

        return ResponseEntity.ok(toAccountResponse(account));
    }

    /**
     * Delete an account listing.
     * Requires authentication and ownership (seller) or admin role.
     *
     * @param id Account ID
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete account", description = "Delete an account listing")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id
    ) {
        Long userId = getAuthenticatedUserId();
        boolean isAdmin = hasAdminRole();
        log.info("DELETE /api/accounts/{} - userId: {}, isAdmin: {}", id, userId, isAdmin);

        // Delegate to service
        accountService.deleteAccount(id, userId, isAdmin);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get account by ID.
     * Public endpoint - no authentication required.
     * Automatically increments view count.
     *
     * @param id Account ID
     * @return Account response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Get account details by ID")
    public ResponseEntity<AccountResponse> getAccountById(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id
    ) {
        log.debug("GET /api/accounts/{}", id);

        // Delegate to service
        Account account = accountService.getAccountById(id);

        return ResponseEntity.ok(toAccountResponse(account));
    }

    /**
     * Increment account view count asynchronously.
     * Public endpoint - fire-and-forget operation.
     * Returns immediately while view count is incremented in the background.
     *
     * @param id Account ID
     * @return HTTP 200 on success
     */
    @PatchMapping("/{id}/view")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Increment view count", description = "Increment account view count asynchronously")
    public void incrementViewCount(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id
    ) {
        log.debug("PATCH /api/accounts/{}/view", id);

        // Delegate to async service method (fire-and-forget)
        accountService.incrementViewCountAsync(id);
    }

    /**
     * Get seller info for an account.
     * Used by chat to start new conversations.
     * Public endpoint - no authentication required.
     *
     * @param id Account ID
     * @return Seller info (id, email, fullName)
     */
    @GetMapping("/{id}/seller-info")
    @Operation(summary = "Get account seller info", description = "Get seller information for an account")
    public ResponseEntity<Map<String, Object>> getSellerInfo(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id
    ) {
        log.debug("GET /api/accounts/{}/seller-info", id);

        // Get account without incrementing views
        Account account = accountService.getAccountByIdWithoutIncrement(id);

        // Build response with seller info
        Map<String, Object> response = new HashMap<>();
        response.put("sellerId", account.getSeller().getId());
        response.put("sellerEmail", account.getSeller().getEmail());
        response.put("sellerFullName", account.getSeller().getFullName());

        return ResponseEntity.ok(response);
    }

    /**
     * Search accounts with filters and pagination.
     * Public endpoint - no authentication required.
     * Results are cached for 10 minutes.
     *
     * @param gameId   Optional filter by game ID
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param status   Optional filter by account status
     * @param page     Page number (default 0)
     * @param limit    Results per page (default 20, max 100)
     * @return Paginated account responses
     */
    @GetMapping
    @Operation(summary = "Search accounts", description = "Search accounts with filters and pagination")
    public ResponseEntity<Map<String, Object>> searchAccounts(
            @Parameter(description = "Filter by game ID")
            @RequestParam(value = "gameId", required = false) Long gameId,

            @Parameter(description = "Minimum price")
            @RequestParam(value = "minPrice", required = false) Double minPrice,

            @Parameter(description = "Maximum price")
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,

            @Parameter(description = "Filter by status")
            @RequestParam(value = "status", required = false) String status,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,

            @Parameter(description = "Results per page")
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit
    ) {
        log.debug("GET /api/accounts - gameId: {}, minPrice: {}, maxPrice: {}, status: {}, page: {}, limit: {}",
                gameId, minPrice, maxPrice, status, page, limit);

        // Convert status string to enum if provided
        AccountStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = AccountStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid AccountStatus provided: {}", status);
            }
        }

        // Create pageable
        int pageNum = (page >= 0) ? page : 0;
        int limitNum = (limit > 0 && limit <= 100) ? limit : 20;
        Pageable pageable = PageRequest.of(pageNum, limitNum, Sort.by("createdAt").descending());

        // Delegate to service
        Page<Account> accountsPage = accountService.searchAccounts(
                gameId, minPrice, maxPrice, statusEnum, pageable
        );

        // Build response map
        Map<String, Object> response = new HashMap<>();
        response.put("content", accountsPage.getContent().stream()
                .map(this::toAccountResponse)
                .collect(Collectors.toList()));
        response.put("totalElements", accountsPage.getTotalElements());
        response.put("totalPages", accountsPage.getTotalPages());
        response.put("currentPage", pageNum);
        response.put("pageSize", limitNum);

        return ResponseEntity.ok(response);
    }

    /**
     * Get authenticated seller's own listings.
     * Requires authentication.
     *
     * @param page Page number (default 0)
     * @param limit Results per page (default 20)
     * @return Paginated account responses for seller
     */
    @GetMapping("/seller/my-accounts")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my accounts", description = "Get authenticated seller's own listings")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> getMyAccounts(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit
    ) {
        Long userId = getAuthenticatedUserId();
        log.debug("GET /api/accounts/seller/my-accounts - userId: {}, page: {}, limit: {}", userId, page, limit);

        // Create pageable
        int pageNum = (page >= 0) ? page : 0;
        int limitNum = (limit > 0 && limit <= 100) ? limit : 20;
        Pageable pageable = PageRequest.of(pageNum, limitNum, Sort.by("createdAt").descending());

        // Get ALL seller's accounts regardless of status (PENDING, APPROVED, REJECTED, etc.)
        Page<Account> accountsPage = accountService.getSellerAccounts(userId, null, pageable);

        // Build response map
        Map<String, Object> response = new HashMap<>();
        response.put("content", accountsPage.getContent().stream()
                .map(this::toAccountResponse)
                .collect(Collectors.toList()));
        response.put("totalElements", accountsPage.getTotalElements());
        response.put("totalPages", accountsPage.getTotalPages());
        response.put("currentPage", pageNum);
        response.put("pageSize", limitNum);

        return ResponseEntity.ok(response);
    }

    // ==================== Helper Methods ====================

    /**
     * Handle image uploads.
     * Validates file type and size, returns URLs.
     * For now, returns placeholder URLs. In production, this would upload to cloud storage.
     *
     * @param files Uploaded image files
     * @return List of image URLs
     */
    private List<String> handleImageUploads(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Arrays.asList();
        }

        // Validate each file
        for (MultipartFile file : files) {
            validateImage(file);
        }

        // For now, return placeholder URLs
        // In production: Upload to Cloudinary/S3 and return actual URLs
        return Arrays.stream(files)
                .map(file -> "https://placeholder.example.com/images/" + file.getOriginalFilename())
                .collect(Collectors.toList());
    }

    /**
     * Validate uploaded image file.
     *
     * @param file Image file to validate
     * @throws BusinessException if validation fails
     */
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") &&
                 !contentType.equals("image/jpg") &&
                 !contentType.equals("image/png"))) {
            throw new BusinessException("Only JPG and PNG images are allowed. Got: " + contentType);
        }

        // Check file size (10MB = 10 * 1024 * 1024 bytes)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException("File size exceeds 10MB limit");
        }
    }

    /**
     * Convert Account entity to AccountResponse DTO.
     *
     * @param account Account entity
     * @return AccountResponse DTO
     */
    private AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .sellerId(account.getSeller().getId())
                .sellerName(account.getSeller().getFullName())
                .sellerEmail(account.getSeller().getEmail())
                .gameId(account.getGame().getId())
                .gameName(account.getGame().getName())
                .gameSlug(account.getGame().getSlug())
                .title(account.getTitle())
                .description(account.getDescription())
                .level(account.getLevel())
                .rank(account.getRank())
                .price(account.getPrice())
                .status(account.getStatus().name())
                .viewsCount(account.getViewsCount())
                .isFeatured(account.isFeatured())
                .images(account.getImages())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    /**
     * Extract authenticated user ID from Spring Security context.
     *
     * @return User ID
     * @throws BusinessException if not authenticated
     */
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException("User must be authenticated to perform this action");
        }

        // In our JWT setup, the username contains the email
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found: " + email));
        
        return user.getId();
    }

    /**
     * Check if authenticated user has ADMIN role.
     *
     * @return true if has ADMIN role
     */
    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
