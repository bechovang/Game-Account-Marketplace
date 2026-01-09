package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.dto.request.AccountSearchRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.graphql.dto.AccountConnection;
import com.gameaccount.marketplace.graphql.dto.AccountEdge;
import com.gameaccount.marketplace.graphql.dto.PageInfo;
import com.gameaccount.marketplace.graphql.dto.PaginatedAccountResponse;
import com.gameaccount.marketplace.service.AccountService;
import com.gameaccount.marketplace.service.PaginationService;
import com.gameaccount.marketplace.util.CursorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    private final CursorUtil cursorUtil;
    private final PaginationService paginationService;

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
        AccountSearchRequest.SortDirection sortDirectionEnum = AccountSearchRequest.SortDirection.DESC;
        try {
            sortDirectionEnum = sortDirection != null && !sortDirection.isEmpty()
                ? AccountSearchRequest.SortDirection.valueOf(sortDirection.toUpperCase())
                : AccountSearchRequest.SortDirection.DESC;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid sort direction provided: {}, using default: DESC", sortDirection);
        }

        // Validate sort field against allowed fields
        if (!accountService.getAllowedSortFields().contains(sortField)) {
            log.warn("Invalid sort field provided: {}, using default: createdAt", sortField);
            sortField = "createdAt";
        }

        // Create sort object
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirectionEnum.name()), sortField);
        Pageable pageable = PageRequest.of(pageNum, limitNum, sort);

        // Build search request
        AccountSearchRequest searchRequest = AccountSearchRequest.builder()
                .gameId(gameId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .status(statusEnum)
                .isFeatured(isFeatured)
                .sortBy(sortField)
                .sortDirection(sortDirectionEnum)
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
     * Query accounts with cursor-based pagination (Relay specification).
     * Provides efficient pagination for large datasets.
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public AccountConnection accountsConnection(@Argument AccountSearchRequest filters,
                                              @Argument String sortBy,
                                              @Argument String sortDirection,
                                              @Argument String after,
                                              @Argument String before,
                                              @Argument Integer first,
                                              @Argument Integer last) {

        log.debug("GraphQL cursor pagination - after: {}, before: {}, first: {}, last: {}",
                after, before, first, last);

        // Validate pagination parameters using PaginationService
        paginationService.validatePaginationParams(first, last, after, before);

        // Determine page size (default 20, max 50)
        int pageSize = first != null ? Math.min(Math.max(first, 1), 50) :
                      last != null ? Math.min(Math.max(last, 1), 50) : 20;

        // Create sort
        Sort sort = createSort(sortBy, sortDirection);

        // Get authenticated user info for filtering
        Long userId = getCurrentUserId();
        String userRole = getCurrentUserRole();

        // Create pageable based on cursor using PaginationService
        Pageable pageable;
        boolean isBackward = before != null;
        if (isBackward) {
            Sort reversedSort = Sort.by(sort.stream()
                .map(order -> new Sort.Order(
                    order.getDirection() == Sort.Direction.ASC ? Sort.Direction.DESC : Sort.Direction.ASC,
                    order.getProperty()))
                .toArray(Sort.Order[]::new));
            pageable = paginationService.createPageableFromBeforeCursor(before, pageSize, reversedSort);
        } else {
            pageable = paginationService.createPageableFromCursor(after, pageSize, sort);
        }

        // Execute query
        Page<Account> accountPage = accountService.searchAccounts(
            filters, userId, userRole, pageable
        );

        // Convert to connection using PaginationService
        return createAccountConnection(accountPage, pageable, pageSize, isBackward, after, before);
    }

    /**
     * Validate cursor pagination parameters.
     */
    private void validateCursorPaginationParams(Integer first, Integer last, String after, String before) {
        if (first != null && last != null) {
            throw new IllegalArgumentException("Cannot specify both 'first' and 'last'");
        }
        if (after != null && before != null) {
            throw new IllegalArgumentException("Cannot specify both 'after' and 'before'");
        }
        if (first != null && before != null) {
            throw new IllegalArgumentException("'before' cannot be used with 'first'");
        }
        if (last != null && after != null) {
            throw new IllegalArgumentException("'after' cannot be used with 'last'");
        }
    }

    /**
     * Determine page size with validation.
     */
    private int determinePageSize(Integer first, Integer last) {
        Integer requestedSize = first != null ? first : last;
        if (requestedSize == null) return 20;
        if (requestedSize < 1) return 1;
        if (requestedSize > 50) return 50;
        return requestedSize;
    }


    /**
     * Create Sort object from parameters.
     */
    private Sort createSort(String sortBy, String sortDirection) {
        String field = sortBy != null && !sortBy.isEmpty() ? sortBy : "createdAt";
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ?
            Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    /**
     * Convert Page to AccountConnection.
     */
    private AccountConnection createAccountConnection(Page<Account> accountPage, Pageable pageable,
                                                    int requestedSize, boolean isBackward,
                                                    String after, String before) {
        List<Account> accounts = accountPage.getContent();

        // Handle cursor-based slicing
        List<Account> slicedAccounts;
        boolean hasNextPage = false;
        boolean hasPreviousPage = pageable.getPageNumber() > 0;

        if (pageable.getPageSize() > determinePageSize(null, null) && accounts.size() > determinePageSize(null, null)) {
            // We requested extra item to check for next page
            slicedAccounts = accounts.subList(0, accounts.size() - 1);
            hasNextPage = true;
        } else {
            slicedAccounts = accounts;
            hasNextPage = accountPage.hasNext();
        }

        // Create edges with cursors
        List<AccountEdge> edges = slicedAccounts.stream()
            .map(account -> {
                String cursor = cursorUtil.encodeCursor(account.getId(),
                    account.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
                return AccountEdge.of(account, cursor);
            })
            .toList();

        // Create page info
        String startCursor = edges.isEmpty() ? null : edges.get(0).getCursor();
        String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor();

        PageInfo pageInfo = PageInfo.builder()
            .hasNextPage(hasNextPage)
            .hasPreviousPage(hasPreviousPage)
            .startCursor(startCursor)
            .endCursor(endCursor)
            .build();

        return AccountConnection.builder()
            .edges(edges)
            .pageInfo(pageInfo)
            .totalCount(accountPage.getTotalElements())
            .build();
    }

    /**
     * Get current authenticated user ID.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            return extractUserIdFromPrincipal(principal);
        }
        return null;
    }

    /**
     * Get current user role.
     */
    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                return extractUserRoleFromAuthorities(
                    ((org.springframework.security.core.userdetails.UserDetails) principal).getAuthorities()
                );
            }
        }
        return "BUYER";
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

        // Strategy 2: Check for userId claim (JWT) - disabled for compatibility
        // JWT support requires spring-boot-starter-oauth2-resource-server dependency
        /*
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
        */

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

    /**
     * Resolve seller field.
     * Accounts are now loaded with JOIN FETCH, so seller relationship is already available.
     */
    @SchemaMapping(typeName = "Account", field = "seller")
    public User seller(Account account) {
        log.debug("Resolving seller field for account: {}", account.getId());
        // With JOIN FETCH in the query, the seller should be loaded
        if (account.getSeller() != null) {
            return account.getSeller();
        }
        // Fallback if somehow not loaded (shouldn't happen with JOIN FETCH)
        log.warn("Seller not loaded for account {}, falling back to service call", account.getId());
        return accountService.getSellerForAccount(account.getId());
    }

    /**
     * Resolve game field.
     * Accounts are now loaded with JOIN FETCH, so game relationship is already available.
     */
    @SchemaMapping(typeName = "Account", field = "game")
    public Game game(Account account) {
        log.debug("Resolving game field for account: {}", account.getId());
        // With JOIN FETCH in the query, the game should be loaded
        if (account.getGame() != null) {
            return account.getGame();
        }
        // Fallback if somehow not loaded (shouldn't happen with JOIN FETCH)
        log.warn("Game not loaded for account {}, falling back to service call", account.getId());
        return accountService.getGameForAccount(account.getId());
    }
}
