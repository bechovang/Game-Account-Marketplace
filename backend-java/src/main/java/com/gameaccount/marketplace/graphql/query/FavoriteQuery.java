package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.graphql.dto.PaginatedAccountResponse;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Query Resolver for favorites.
 * Uses Spring Boot 3's native @QueryMapping annotation.
 * Delegates all business logic to FavoriteService (shared service layer pattern).
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class FavoriteQuery {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    /**
     * Get user's favorite accounts with pagination.
     * Query: favorites(page: Int, limit: Int, sortBy: String, sortDirection: String): PaginatedAccountResponse
     *
     * Note: sortBy and sortDirection parameters are accepted for frontend compatibility
     * but favorites are currently returned in the order they were added.
     *
     * @param page Page number (default 0)
     * @param limit Page size (default 20)
     * @param sortBy Sort field (for compatibility - currently not used)
     * @param sortDirection Sort direction (for compatibility - currently not used)
     * @return Paginated response of favorite Account objects
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public PaginatedAccountResponse favorites(@Argument Integer page,
                                               @Argument Integer limit,
                                               @Argument String sortBy,
                                               @Argument String sortDirection) {
        Long userId = getAuthenticatedUserId();
        log.debug("GraphQL favorites query - userId: {}, page: {}, limit: {}, sortBy: {}, sortDirection: {}",
                userId, page, limit, sortBy, sortDirection);

        // Get all favorites for the user
        List<Account> allFavorites = favoriteService.getUserFavorites(userId);

        // Apply pagination in-memory (since FavoriteService returns a list)
        int pageNum = (page != null && page >= 0) ? page : 0;
        int limitNum = (limit != null && limit > 0) ? Math.min(limit, 100) : 20;

        int totalElements = allFavorites.size();
        int totalPages = (int) Math.ceil((double) totalElements / limitNum);
        int startIndex = pageNum * limitNum;
        int endIndex = Math.min(startIndex + limitNum, totalElements);

        // Get the page content
        List<Account> pageContent;
        if (startIndex >= totalElements) {
            pageContent = List.of();
        } else {
            pageContent = allFavorites.subList(startIndex, endIndex);
        }

        // Build paginated response
        return PaginatedAccountResponse.builder()
                .content(pageContent)
                .totalElements((long) totalElements)
                .totalPages(totalPages)
                .currentPage(pageNum)
                .pageSize(limitNum)
                .build();
    }

    // ==================== Helper Methods ====================

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
