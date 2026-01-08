package com.gameaccount.marketplace.graphql.resolver;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Field Resolver for Account type.
 * Resolves custom fields on Account that require additional logic or services.
 * Uses Spring Boot 3's native @SchemaMapping annotation.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountFieldResolver {

    private final FavoriteRepository favoriteRepository;

    /**
     * Resolve isFavorited field for Account type.
     * Returns true if the current authenticated user has favorited this account.
     * Returns false if not authenticated or not favorited.
     *
     * Note: This method uses individual queries per account. For batch loading
     * to prevent N+1 queries in list views, see FavoriteBatchLoader which can
     * be integrated when GraphQL DataLoaderRegistry support is added.
     *
     * @param account The account being resolved
     * @return true if favorited by current user, false otherwise
     */
    @SchemaMapping(typeName = "Account", field = "isFavorited")
    public boolean isFavorited(Account account) {
        // Get authenticated user ID if available
        Long userId = getUserIdIfAuthenticated();

        // If no authenticated user, return false
        if (userId == null) {
            return false;
        }

        // Check if account is favorited by this user
        return favoriteRepository.existsByUserIdAndAccountId(userId, account.getId());
    }

    /**
     * Get authenticated user ID if available, returns null if not authenticated.
     *
     * @return User ID or null if not authenticated
     */
    private Long getUserIdIfAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            log.error("Failed to parse user ID from authentication: {}", authentication.getName());
            return null;
        }
    }
}
