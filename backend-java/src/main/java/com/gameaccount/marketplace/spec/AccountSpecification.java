package com.gameaccount.marketplace.spec;

import com.gameaccount.marketplace.dto.request.AccountSearchRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for dynamic Account query building.
 * Provides flexible filtering for account search with multiple optional parameters.
 */
public class AccountSpecification {

    /**
     * Build a dynamic specification for account search based on provided filters.
     * All parameters are optional - null values are ignored in the query.
     *
     * @param gameId Optional game filter
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param minLevel Optional minimum level filter
     * @param maxLevel Optional maximum level filter
     * @param rank Optional rank filter (partial, case-insensitive)
     * @param status Optional account status filter
     * @param isFeatured Optional featured flag filter
     * @param searchText Optional full-text search on title and description
     * @param sellerId Optional seller filter (for viewing own listings)
     * @return Specification for dynamic query building
     */
    public static Specification<Account> buildSearchSpecification(
            Long gameId,
            Double minPrice,
            Double maxPrice,
            Integer minLevel,
            Integer maxLevel,
            String rank,
            AccountStatus status,
            Boolean isFeatured,
            String searchText,
            Long sellerId) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Game filter
            if (gameId != null) {
                predicates.add(cb.equal(root.get("game").get("id"), gameId));
            }

            // Price range filter
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // Level range filter
            if (minLevel != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("level"), minLevel));
            }
            if (maxLevel != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("level"), maxLevel));
            }

            // Rank filter (case-insensitive partial match)
            if (rank != null && !rank.trim().isEmpty()) {
                predicates.add(cb.like(
                    cb.lower(root.get("rank")),
                    "%" + rank.toLowerCase() + "%"
                ));
            }

            // Status filter
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Featured filter
            if (isFeatured != null) {
                predicates.add(cb.equal(root.get("isFeatured"), isFeatured));
            }

            // Full-text search on title and description
            if (searchText != null && !searchText.trim().isEmpty()) {
                String searchPattern = "%" + searchText.toLowerCase() + "%";
                Predicate titleMatch = cb.like(
                    cb.lower(root.get("title")),
                    searchPattern
                );
                Predicate descMatch = cb.like(
                    cb.lower(root.get("description")),
                    searchPattern
                );
                predicates.add(cb.or(titleMatch, descMatch));
            }

            // Seller filter (for viewing own pending listings)
            if (sellerId != null) {
                predicates.add(cb.equal(root.get("seller").get("id"), sellerId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Build a specification from AccountSearchRequest DTO.
     * Convenience method that extracts all parameters from the request object.
     *
     * @param request The search request containing all filter parameters
     * @return Specification for dynamic query building
     */
    public static Specification<Account> fromSearchRequest(AccountSearchRequest request) {
        return buildSearchSpecification(
            request.getGameId(),
            request.getMinPrice(),
            request.getMaxPrice(),
            request.getMinLevel(),
            request.getMaxLevel(),
            request.getRank(),
            request.getStatus(),
            request.getIsFeatured(),
            request.getSearchText(),
            request.getSellerId()
        );
    }
}
