package com.gameaccount.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for review data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private UserResponse reviewer;
    private UserResponse targetUser;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
