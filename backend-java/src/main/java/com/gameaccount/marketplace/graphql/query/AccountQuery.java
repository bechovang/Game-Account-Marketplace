package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.dto.request.AccountSearchRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.graphql.dto.PaginatedAccountResponse;
import com.gameaccount.marketplace.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Query Resolver for Account entities.
 * Uses Spring Boot 3's native @QueryMapping annotation.
 * Delegates all business logic to AccountService (shared service layer pattern).
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountQuery {

    private final AccountService accountService;

    /**
     * Query accounts with optional filters, sorting, and pagination.
     * Delegates to AccountService.searchAccounts() with AccountSearchRequest.
     */
    @QueryMapping
    public PaginatedAccountResponse accounts(@Argument Long gameId,
                                             @Argument Double minPrice,
                                             @Argument Double maxPrice,
                                             @Argument String status,
                                             @Argument Boolean isFeatured,
                                             @Argument String sortBy,
                                             @Argument String sortDirection,
                                             @Argument Integer page,
                                             @Argument Integer limit) {
        log.debug("GraphQL accounts query - gameId: {}, minPrice: {}, maxPrice: {}, status: {}, isFeatured: {}, sortBy: {}, sortDirection: {}, page: {}, limit: {}",
                gameId, minPrice, maxPrice, status, isFeatured, sortBy, sortDirection, page, limit);

        // Set defaults for pagination
        int pageNum = (page != null && page >= 0) ? page : 0;
        int limitNum = (limit != null && limit > 0) ? Math.min(limit, 100) : 20;

        // Convert status string to enum if provided
        AccountStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = AccountStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid AccountStatus provided: {}", status);
            }
        }

        // Validate and set sort parameters
        String sortField = sortBy != null && !sortBy.isEmpty() ? sortBy : "createdAt";
        String sortDir = sortDirection != null && !sortDirection.isEmpty()
            ? sortDirection.toUpperCase()
            : "DESC";

        // Validate sort field against allowed fields
        if (!accountService.getAllowedSortFields().contains(sortField)) {
            log.warn("Invalid sort field provided: {}, using default: createdAt", sortField);
            sortField = "createdAt";
        }

        // Create sort object
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortField);
        Pageable pageable = PageRequest.of(pageNum, limitNum, sort);

        // Build search request
        AccountSearchRequest searchRequest = AccountSearchRequest.builder()
                .gameId(gameId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .status(statusEnum)
                .isFeatured(isFeatured)
                .sortBy(sortField)
                .sortDirection(sortDir)
                .build();

        // Get authenticated user info for role-based filtering
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        String userRole = "BUYER"; // Default to public/buyer access

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            // Extract user ID and role from authentication
            Object principal = authentication.getPrincipal();
            userId = extractUserIdFromPrincipal(principal);
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                userRole = extractUserRoleFromAuthorities(
                    ((org.springframework.security.core.userdetails.UserDetails) principal).getAuthorities()
                );
            }
        }

        // Delegate to service layer with role-based filtering
        Page<Account> accountsPage = accountService.searchAccounts(
                searchRequest, userId, userRole, pageable
        );

        // Wrap response for GraphQL
        return PaginatedAccountResponse.builder()
                .content(accountsPage.getContent())
                .totalElements(accountsPage.getTotalElements())
                .totalPages(accountsPage.getTotalPages())
                .currentPage(pageNum)
                .pageSize(limitNum)
                .build();
    }

    /**
     * Get a single account by ID.
     * Delegates to AccountService.getAccountByIdWithoutIncrement()
     * Does NOT increment view count - that's handled by separate PATCH endpoint.
     * Results are cached for 10 minutes.
     *
     * Note: Authentication required (@PreAuthorize) to prevent unauthorized access.
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    @Cacheable(value = "accounts", key = "#id")
    public Account account(@Argument Long id) {
        log.debug("GraphQL account query - id: {}", id);

        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Valid account ID is required");
        }

        // Delegate to service layer - fetch WITHOUT incrementing view count
        // View count is incremented separately via PATCH /api/accounts/{id}/view
        return accountService.getAccountByIdWithoutIncrement(id);
    }

    /**
     * Helper method to extract user ID from authentication principal.
     * Supports multiple authentication strategies:
     * 1. JWT claims with userId claim
     * 2. User entity with getId() method
     * 3. Legacy username parsing ("user_123" format)
     *
     * TODO: Implement proper JWT claim extraction for production
     */
    private Long extractUserIdFromPrincipal(Object principal) {
        if (principal == null) {
            return null;
        }

        // Strategy 1: Check if principal is a User entity with getId()
        try {
            if (principal.getClass().getMethod("getId") != null) {
                Object id = principal.getClass().getMethod("getId").invoke(principal);
                if (id instanceof Long) {
                    return (Long) id;
                }
                if (id instanceof Integer) {
                    return ((Integer) id).longValue();
                }
                if (id instanceof String) {
                    return Long.parseLong((String) id);
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract ID via getId() method: {}", e.getMessage());
        }

        // Strategy 2: Check for userId claim (JWT)
        try {
            if (principal instanceof org.springframework.security.oauth2.jwt.Jwt) {
                org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) principal;
                Object userIdClaim = jwt.getClaims().get("userId");
                if (userIdClaim != null) {
                    return Long.valueOf(userIdClaim.toString());
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract userId from JWT: {}", e.getMessage());
        }

        // Strategy 3: Legacy username parsing (fallback)
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            if (username != null && username.startsWith("user_")) {
                try {
                    return Long.parseLong(username.substring(5));
                } catch (NumberFormatException e) {
                    log.warn("Could not parse user ID from username format 'user_XXX': {}", username);
                }
            }
        }

        log.warn("Unable to extract user ID from principal of type: {}", principal.getClass().getSimpleName());
        return null;
    }

    /**
     * Helper method to extract user ID from username.
     * @deprecated Use extractUserIdFromPrincipal instead for better security.
     */
    @Deprecated
    private Long extractUserIdFromUsername(String username) {
        if (username == null) {
            return null;
        }
        try {
            if (username.startsWith("user_")) {
                return Long.parseLong(username.substring(5));
            }
        } catch (NumberFormatException e) {
            log.warn("Could not extract user ID from username: {}", username);
        }
        return null;
    }

    /**
     * Helper method to extract user role from authorities.
     * Prioritizes ADMIN > SELLER > BUYER hierarchy.
     */
    private String extractUserRoleFromAuthorities(java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return "BUYER";
        }

        // Check for ADMIN first (highest priority)
        for (org.springframework.security.core.GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }
            if ("ADMIN".equals(role)) {
                return "ADMIN";
            }
        }

        // Check for SELLER next
        for (org.springframework.security.core.GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }
            if ("SELLER".equals(role)) {
                return "SELLER";
            }
        }

        // Default to BUYER
        return "BUYER";
    }
}
