package com.gameaccount.marketplace.graphql.dto;

import com.gameaccount.marketplace.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper for paginated account query results.
 * Maps Spring Data Page to GraphQL pagination format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedAccountResponse {

    /**
     * The actual account data for this page
     */
    private List<Account> content;

    /**
     * Total number of elements across all pages
     */
    private Long totalElements;

    /**
     * Total number of pages available
     */
    private Integer totalPages;

    /**
     * Current page number (0-indexed)
     */
    private Integer currentPage;

    /**
     * Number of elements per page
     */
    private Integer pageSize;
}
