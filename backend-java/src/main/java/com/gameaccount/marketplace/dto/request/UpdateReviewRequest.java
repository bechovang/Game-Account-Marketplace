package com.gameaccount.marketplace.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing review.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewRequest {

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    @Pattern(regexp = "\\S.*", message = "Comment cannot be empty or whitespace only")
    private String comment;
}
