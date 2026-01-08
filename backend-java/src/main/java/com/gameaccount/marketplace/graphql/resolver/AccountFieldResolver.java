package com.gameaccount.marketplace.graphql.resolver;

import com.gameaccount.marketplace.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

/**
 * GraphQL Field Resolver for Account type.
 * Resolves custom fields on Account that require additional logic or services.
 * Uses Spring Boot 3's native @SchemaMapping annotation with DataLoader integration.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountFieldResolver {

    /**
     * Resolve isFavorited field using FavoriteBatchLoader.
     * Batches favorite checks to prevent N+1 queries.
     * Spring GraphQL automatically injects the DataLoader bean.
     */
    @SchemaMapping(typeName = "Account", field = "isFavorited")
    public CompletableFuture<Boolean> isFavorited(Account account, DataLoader<Long, Boolean> favoriteLoader) {
        log.debug("Resolving isFavorited field for account: {} using DataLoader", account.getId());
        // FavoriteBatchLoader gets the user ID from security context
        return favoriteLoader.load(account.getId());
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
