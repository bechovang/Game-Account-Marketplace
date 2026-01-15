package com.gameaccount.marketplace.graphql.mutation;

import com.gameaccount.marketplace.dto.request.CreateAccountRequest;
import com.gameaccount.marketplace.dto.request.UpdateAccountRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.graphql.dto.CreateAccountInput;
import com.gameaccount.marketplace.graphql.dto.UpdateAccountInput;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Mutation Resolver for Account entities.
 * Uses Spring Boot 3's native @MutationMapping annotation.
 * Delegates all business logic to AccountService (shared service layer pattern).
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountMutation {

    private final AccountService accountService;
    private final UserRepository userRepository;

    /**
     * Create a new account listing.
     * Requires authentication (SELLER or ADMIN role).
     */
    @MutationMapping
    public Account createAccount(@Valid @Argument CreateAccountInput input) {
        Long userId = getAuthenticatedUserId();
        log.info("GraphQL createAccount mutation - userId: {}, gameId: {}, title: {}",
                userId, input.getGameId(), input.getTitle());

        // Map GraphQL input to service DTO
        CreateAccountRequest request = CreateAccountRequest.builder()
                .gameId(input.getGameId())
                .title(input.getTitle())
                .description(input.getDescription())
                .level(input.getLevel())
                .rank(input.getRank())
                .price(input.getPrice())
                .images(input.getImages())
                .username(input.getUsername())
                .password(input.getPassword())
                .build();

        // Delegate to service layer
        return accountService.createAccount(request, userId);
    }

    /**
     * Update an existing account listing.
     * Requires authentication and ownership (or admin role).
     */
    @MutationMapping
    public Account updateAccount(@Argument Long id, @Valid @Argument UpdateAccountInput input) {
        Long userId = getAuthenticatedUserId();
        log.info("GraphQL updateAccount mutation - accountId: {}, userId: {}", id, userId);

        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Valid account ID is required");
        }

        // Map GraphQL input to service DTO
        UpdateAccountRequest request = UpdateAccountRequest.builder()
                .title(input.getTitle())
                .description(input.getDescription())
                .level(input.getLevel())
                .rank(input.getRank())
                .price(input.getPrice())
                .images(input.getImages())
                .build();

        // Delegate to service layer (handles ownership check)
        return accountService.updateAccount(id, request, userId);
    }

    /**
     * Delete an account listing.
     * Requires authentication and ownership (or admin role).
     */
    @MutationMapping
    public Boolean deleteAccount(@Argument Long id) {
        Long userId = getAuthenticatedUserId();
        boolean isAdmin = hasAdminRole();
        log.info("GraphQL deleteAccount mutation - accountId: {}, userId: {}, isAdmin: {}", id, userId, isAdmin);

        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Valid account ID is required");
        }

        // Delegate to service layer (handles ownership/admin check)
        accountService.deleteAccount(id, userId, isAdmin);
        return true;
    }

    /**
     * Approve a pending account listing.
     * Requires ADMIN role.
     */
    @MutationMapping
    public Account approveAccount(@Argument Long id) {
        Long userId = getAuthenticatedUserId();
        log.info("GraphQL approveAccount mutation - accountId: {}, adminId: {}", id, userId);

        if (!hasAdminRole()) {
            throw new BusinessException("Only administrators can approve accounts");
        }

        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Valid account ID is required");
        }

        // Delegate to service layer
        return accountService.approveAccount(id);
    }

    /**
     * Reject a pending account listing.
     * Requires ADMIN role.
     */
    @MutationMapping
    public Account rejectAccount(@Argument Long id, @Argument String reason) {
        Long userId = getAuthenticatedUserId();
        log.info("GraphQL rejectAccount mutation - accountId: {}, adminId: {}, reason: {}", id, userId, reason);

        if (!hasAdminRole()) {
            throw new BusinessException("Only administrators can reject accounts");
        }

        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Valid account ID is required");
        }

        // Delegate to service layer
        return accountService.rejectAccount(id, reason);
    }

    // ==================== Helper Methods ====================

    /**
     * Extract authenticated user ID from Spring Security context.
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
