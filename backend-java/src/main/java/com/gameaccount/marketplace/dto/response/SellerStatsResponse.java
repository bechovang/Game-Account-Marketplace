package com.gameaccount.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for seller statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerStatsResponse {

    private Double averageRating;
    private Long totalReviews;
}
