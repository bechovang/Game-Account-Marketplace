package com.gameaccount.marketplace.graphql.resolver;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.AccountService;
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
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountFieldResolver {

    private final AccountService accountService;
    private final UserRepository userRepository;

    /**
     * Resolve isFavorited field.
     * NOTE: DataLoader temporarily disabled - checking directly via service.
     * TODO: Re-enable DataLoader batching after upgrading to Spring Boot 3.3+
     *
     * @param account The account being resolved
     * @return true if this account is favorited by current user, false otherwise
     */
    @SchemaMapping(typeName = "Account", field = "isFavorited")
    public boolean isFavorited(Account account) {
        log.debug("Resolving isFavorited field for account: {}", account.getId());

        Long userId = getUserIdIfAuthenticated();
        if (userId == null) {
            return false;
        }

        return accountService.isAccountFavoritedByUser(account.getId(), userId);
    }

    /**
     * Get authenticated user ID if available, returns null if not authenticated.
     * JWT token contains email as subject, so we look up user by email.
     *
     * @return User ID or null if not authenticated
     */
    private Long getUserIdIfAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElse(null);
    }
}
