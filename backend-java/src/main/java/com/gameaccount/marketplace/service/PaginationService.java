package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.util.CursorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling cursor-based pagination logic.
 * Provides utilities for encoding/decoding cursors and managing pagination state.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaginationService {

    private final CursorUtil cursorUtil;

    /**
     * Creates a Pageable from cursor parameters for forward pagination.
     * @param after Base64 encoded cursor for forward pagination
     * @param pageSize Number of items per page
     * @param sort Sort specification
     * @return Pageable configured for cursor-based pagination
     */
    public Pageable createPageableFromCursor(String after, Integer pageSize, Sort sort) {
        int size = Math.min(pageSize != null ? pageSize : 20, 50); // Max 50 items per page

        if (after != null && !after.trim().isEmpty()) {
            // Forward pagination from cursor - request extra item to check if more exist
            return PageRequest.of(0, size + 1, sort);
        } else {
            // First page
            return PageRequest.of(0, size, sort);
        }
    }

    /**
     * Creates a Pageable from cursor parameters for backward pagination.
     * @param before Base64 encoded cursor for backward pagination
     * @param pageSize Number of items per page
     * @param sort Sort specification
     * @return Pageable configured for backward cursor-based pagination
     */
    public Pageable createPageableFromBeforeCursor(String before, Integer pageSize, Sort sort) {
        int size = Math.min(pageSize != null ? pageSize : 20, 50);

        if (before != null && !before.trim().isEmpty()) {
            // Backward pagination from cursor - request extra item to check if more exist
            Sort reversedSort = Sort.by(sort.stream()
                .map(order -> new Sort.Order(
                    order.getDirection() == Sort.Direction.ASC ? Sort.Direction.DESC : Sort.Direction.ASC,
                    order.getProperty()))
                .toArray(Sort.Order[]::new));
            return PageRequest.of(0, size + 1, reversedSort);
        } else {
            // This shouldn't happen in normal usage
            return PageRequest.of(0, size, sort);
        }
    }

    /**
     * Determines if there are more pages after the current page.
     * @param page Current page of results
     * @param requestedSize Original requested page size (without extra item)
     * @return true if there are more pages available
     */
    public boolean hasNextPage(Page<?> page, int requestedSize) {
        // If we requested an extra item and got it, there are more pages
        return page.getContent().size() > requestedSize;
    }

    /**
     * Determines if there are previous pages before the current page.
     * @param after Cursor used for forward pagination (null means first page)
     * @param before Cursor used for backward pagination (null means not backward pagination)
     * @return true if there are previous pages available
     */
    public boolean hasPreviousPage(String after, String before) {
        // If we're paginating forward from a cursor, there are previous pages
        // If we're paginating backward, we need to check if we have more results than requested
        return after != null && !after.trim().isEmpty();
    }

    /**
     * Creates the next cursor for forward pagination.
     * @param items List of items in the current page
     * @param requestedSize Original requested page size
     * @return Base64 encoded cursor or null if no more pages
     */
    public String createNextCursor(List<?> items, int requestedSize) {
        if (items.size() <= requestedSize) {
            return null; // No more pages
        }

        // Use the last item of the requested size as the cursor
        Object lastItem = items.get(requestedSize - 1);
        return createCursorFromEntity(lastItem);
    }

    /**
     * Creates the previous cursor for backward pagination.
     * @param items List of items in the current page
     * @return Base64 encoded cursor or null if no previous pages
     */
    public String createPreviousCursor(List<?> items) {
        if (items.isEmpty()) {
            return null;
        }

        // Use the first item as the cursor for backward pagination
        Object firstItem = items.get(0);
        return createCursorFromEntity(firstItem);
    }

    /**
     * Slices the page content to remove the extra item used for pagination checks.
     * @param items Full list of items (may include extra item)
     * @param requestedSize Original requested page size
     * @return Sliced list containing only the requested number of items
     */
    public <T> List<T> slicePageContent(List<T> items, int requestedSize) {
        if (items.size() <= requestedSize) {
            return items;
        }
        return items.subList(0, requestedSize);
    }

    /**
     * Validates pagination parameters.
     * @param first Number of items for forward pagination
     * @param last Number of items for backward pagination
     * @param after Cursor for forward pagination
     * @param before Cursor for backward pagination
     * @throws IllegalArgumentException if parameters are invalid
     */
    public void validatePaginationParams(Integer first, Integer last, String after, String before) {
        if (first != null && last != null) {
            throw new IllegalArgumentException("Cannot specify both 'first' and 'last'");
        }
        if (after != null && before != null) {
            throw new IllegalArgumentException("Cannot specify both 'after' and 'before'");
        }
        if (first != null && before != null) {
            throw new IllegalArgumentException("'before' cannot be used with 'first'");
        }
        if (last != null && after != null) {
            throw new IllegalArgumentException("'after' cannot be used with 'last'");
        }

        // Validate page sizes
        if (first != null && (first < 1 || first > 50)) {
            throw new IllegalArgumentException("'first' must be between 1 and 50");
        }
        if (last != null && (last < 1 || last > 50)) {
            throw new IllegalArgumentException("'last' must be between 1 and 50");
        }

        // Validate cursors
        if (after != null && !cursorUtil.isValidCursor(after)) {
            throw new IllegalArgumentException("Invalid 'after' cursor format");
        }
        if (before != null && !cursorUtil.isValidCursor(before)) {
            throw new IllegalArgumentException("Invalid 'before' cursor format");
        }
    }

    /**
     * Creates a cursor string from an entity.
     * This is a generic implementation - subclasses should override for specific entity types.
     * @param entity The entity to create cursor from
     * @return Base64 encoded cursor string
     */
    protected String createCursorFromEntity(Object entity) {
        // Default implementation - assumes entity has getId() and getCreatedAt() methods
        try {
            Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);
            Object createdAt = entity.getClass().getMethod("getCreatedAt").invoke(entity);

            Long timestamp;
            if (createdAt instanceof java.time.LocalDateTime) {
                timestamp = ((java.time.LocalDateTime) createdAt)
                    .toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
            } else if (createdAt instanceof java.time.Instant) {
                timestamp = ((java.time.Instant) createdAt).toEpochMilli();
            } else {
                throw new IllegalArgumentException("Unsupported date type: " + createdAt.getClass());
            }

            return cursorUtil.encodeCursor(id, timestamp);
        } catch (Exception e) {
            log.error("Failed to create cursor from entity: {}", entity, e);
            throw new RuntimeException("Failed to create cursor from entity", e);
        }
    }

    /**
     * Decodes a cursor and extracts the ID and timestamp.
     * @param cursor Base64 encoded cursor string
     * @return CursorData containing ID and timestamp
     */
    public CursorUtil.CursorData decodeCursor(String cursor) {
        return cursorUtil.decodeCursor(cursor);
    }
}
