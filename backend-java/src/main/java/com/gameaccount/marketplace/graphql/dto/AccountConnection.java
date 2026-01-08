package com.gameaccount.marketplace.graphql.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * GraphQL Connection type for cursor-based pagination of accounts.
 * Follows Relay specification for GraphQL pagination.
 */
@Data
@Builder
public class AccountConnection {

    /**
     * List of edges containing nodes and cursors.
     */
    private List<AccountEdge> edges;

    /**
     * Pagination metadata.
     */
    private PageInfo pageInfo;

    /**
     * Total count of items (for display purposes).
     * Note: This may be expensive to compute for large datasets.
     */
    private long totalCount;

    /**
     * Convenience method to get accounts from edges.
     */
    public List<?> getNodes() {
        return edges != null ? edges.stream()
            .map(AccountEdge::getNode)
            .toList() : List.of();
    }
}
