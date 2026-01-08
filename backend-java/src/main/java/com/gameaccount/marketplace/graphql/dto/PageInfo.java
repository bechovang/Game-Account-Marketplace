package com.gameaccount.marketplace.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GraphQL PageInfo type for cursor-based pagination metadata.
 * Follows Relay specification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {

    /**
     * Whether there are more items after the current page.
     */
    private boolean hasNextPage;

    /**
     * Whether there are more items before the current page.
     */
    private boolean hasPreviousPage;

    /**
     * Cursor for the first item in the current page.
     * Null if the page is empty.
     */
    private String startCursor;

    /**
     * Cursor for the last item in the current page.
     * Null if the page is empty.
     */
    private String endCursor;

    /**
     * Convenience method to check if this page has any items.
     */
    public boolean hasItems() {
        return startCursor != null && endCursor != null;
    }
}
