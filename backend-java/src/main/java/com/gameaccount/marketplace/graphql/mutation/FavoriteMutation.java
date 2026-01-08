package com.gameaccount.marketplace.graphql.mutation;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Favorite;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Mutation Resolver for favorites.
 * Uses Spring Boot 3's native @MutationMapping annotation.
 * Delegates all business logic to FavoriteService (shared service layer pattern).
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class FavoriteMutation {

    private final FavoriteService favoriteService;

    /**
     * Add an account to favorites.
     * Mutation: addToFavorites(accountId: ID!): Account
     *
     * @param accountId ID of account to favorite
     * @return Favorited Account
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Account addToFavorites(@Argument Long accountId) {
        Long userId = getAuthenticatedUserId();
        log.info("GraphQL addToFavorites mutation - userId: {}, accountId: {}", userId, accountId);

        // Delegate to service layer
        Favorite favorite = favoriteService.addToFavorites(accountId, userId);
        return favorite.getAccount();
    }

    /**
     * Remove an account from favorites.
     * Mutation: removeFromFavorites(accountId: ID!): Boolean
     *
     * @param accountId ID of account to unfavorite
     * @return true if successful
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean removeFromFavorites(@Argument Long accountId) {
        Long userId = getAuthenticatedUserId();
        log.info("GraphQL removeFromFavorites mutation - userId: {}, accountId: {}", userId, accountId);

        // Delegate to service layer
        favoriteService.removeFromFavorites(accountId, userId);
        return true;
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

        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            log.error("Failed to parse user ID from authentication: {}", authentication.getName());
            throw new BusinessException("Invalid authentication token");
        }
    }
}
