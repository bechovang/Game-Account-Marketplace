package com.gameaccount.marketplace.dto.request;

import com.gameaccount.marketplace.entity.Account.AccountStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for advanced account search with multiple filters.
 * Supports filtering by game, price range, level range, rank, status,
 * featured flag, full-text search, and sorting options.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSearchRequest {

    /** Filter by specific game ID */
    private Long gameId;

    /** Minimum price filter (inclusive) */
    @DecimalMin(value = "0.0", message = "Minimum price cannot be negative")
    private Double minPrice;

    /** Maximum price filter (inclusive) */
    @DecimalMin(value = "0.0", message = "Maximum price cannot be negative")
    private Double maxPrice;

    /** Minimum level filter (inclusive) */
    @Min(value = 0, message = "Minimum level cannot be negative")
    private Integer minLevel;

    /** Maximum level filter (inclusive) */
    @Min(value = 0, message = "Maximum level cannot be negative")
    private Integer maxLevel;

    /** Rank filter (partial match, case-insensitive) */
    private String rank;

    /** Account status filter */
    private AccountStatus status;

    /** Featured accounts filter */
    private Boolean isFeatured;

    /** Full-text search on title and description */
    private String searchText;

    /** Seller ID filter (for viewing own listings) */
    private Long sellerId;

    /** Sort field: price, level, createdAt */
    @Pattern(regexp = "price|level|createdAt|", message = "sortBy must be one of: price, level, createdAt")
    private String sortBy;

    /** Sort direction: ASC or DESC */
    private SortDirection sortDirection;

    /** Sort direction enum */
    public enum SortDirection {
        ASC, DESC
    }
}
