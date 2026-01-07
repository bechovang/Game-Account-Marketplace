package com.gameaccount.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for Account entity.
 * Used in REST API responses to avoid exposing JPA entities directly.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private Long id;
    private Long sellerId;
    private String sellerName;
    private String sellerEmail;
    private Long gameId;
    private String gameName;
    private String gameSlug;
    private String title;
    private String description;
    private Integer level;
    private String rank;
    private Double price;
    private String status;
    private Integer viewsCount;
    private Boolean isFeatured;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
