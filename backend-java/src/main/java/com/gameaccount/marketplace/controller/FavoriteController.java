package com.gameaccount.marketplace.controller;

import com.gameaccount.marketplace.dto.request.AddFavoriteRequest;
import com.gameaccount.marketplace.dto.response.AccountResponse;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Favorite;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing user favorites.
 * Provides endpoints for adding, removing, and retrieving favorite accounts.
 * Follows shared service layer pattern - delegates all business logic to FavoriteService.
 */
@Slf4j
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Favorites management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    /**
     * Add an account to user's favorites.
     * POST /api/favorites
     *
     * @param request Request containing accountId
     * @return AccountResponse with HTTP 201 on success
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add to favorites", description = "Add an account to user's favorites")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AccountResponse> addFavorite(@Valid @RequestBody AddFavoriteRequest request) {
        Long userId = getAuthenticatedUserId();
        log.info("POST /api/favorites - userId: {}, accountId: {}", userId, request.getAccountId());

        // Delegate to service
        Favorite favorite = favoriteService.addToFavorites(request.getAccountId(), userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toAccountResponse(favorite.getAccount()));
    }

    /**
     * Get user's favorite accounts with pagination.
     * GET /api/favorites?page=0&limit=20
     *
     * @param page Page number (default 0)
     * @param limit Page size (default 20)
     * @return List of AccountResponse
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get favorites", description = "Get user's favorite accounts with pagination")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<AccountResponse>> getFavorites(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,

            @Parameter(description = "Results per page")
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit
    ) {
        Long userId = getAuthenticatedUserId();
        log.debug("GET /api/favorites - userId: {}, page: {}, limit: {}", userId, page, limit);

        // Validate pagination parameters
        if (page < 0) {
            throw new BusinessException("Page number must be >= 0");
        }
        if (limit < 1 || limit > 100) {
            throw new BusinessException("Limit must be between 1 and 100");
        }

        // Delegate to service
        List<Account> accounts = favoriteService.getUserFavorites(userId);

        // Apply pagination using Stream
        List<AccountResponse> response = accounts.stream()
                .skip(page * limit)
                .limit(limit)
                .map(this::toAccountResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Remove an account from user's favorites.
     * DELETE /api/favorites/{accountId}
     *
     * @param accountId ID of account to unfavorite
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{accountId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Remove from favorites", description = "Remove an account from user's favorites")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> removeFavorite(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long accountId
    ) {
        Long userId = getAuthenticatedUserId();
        log.info("DELETE /api/favorites/{} - userId: {}", accountId, userId);

        // Delegate to service
        favoriteService.removeFromFavorites(accountId, userId);

        return ResponseEntity.noContent().build();
    }

    // ==================== Helper Methods ====================

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
}
