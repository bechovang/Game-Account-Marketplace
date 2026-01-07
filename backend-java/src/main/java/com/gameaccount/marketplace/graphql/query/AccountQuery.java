package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.graphql.dto.PaginatedAccountResponse;
import com.gameaccount.marketplace.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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
     * Query accounts with optional filters and pagination.
     * Delegates to AccountService.searchAccounts()
     */
    @QueryMapping
    public PaginatedAccountResponse accounts(Long gameId, Double minPrice, Double maxPrice,
                                             String status, Integer page, Integer limit) {
        log.debug("GraphQL accounts query - gameId: {}, minPrice: {}, maxPrice: {}, status: {}, page: {}, limit: {}",
                gameId, minPrice, maxPrice, status, page, limit);

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

        // Create pageable object
        Pageable pageable = PageRequest.of(pageNum, limitNum);

        // Delegate to service layer
        Page<Account> accountsPage = accountService.searchAccounts(
                gameId, minPrice, maxPrice, statusEnum, pageable
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
     * Delegates to AccountService.getAccountById()
     * Automatically increments view count.
     */
    @QueryMapping
    public Account account(Long id) {
        log.debug("GraphQL account query - id: {}", id);

        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Valid account ID is required");
        }

        // Delegate to service layer
        return accountService.getAccountById(id);
    }
}
