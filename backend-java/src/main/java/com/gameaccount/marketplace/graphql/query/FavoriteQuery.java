package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
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
     * Get user's favorite accounts.
     * Query: favorites(page: Int, limit: Int): [Account]
     *
     * @param page Page number (default 0)
     * @param limit Page size (default 20)
     * @return List of favorite Account objects
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Account> favorites(@Argument Integer page, @Argument Integer limit) {
        Long userId = getAuthenticatedUserId();
        log.debug("GraphQL favorites query - userId: {}, page: {}, limit: {}", userId, page, limit);

        // Delegate to service layer
        return favoriteService.getUserFavorites(userId);
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
