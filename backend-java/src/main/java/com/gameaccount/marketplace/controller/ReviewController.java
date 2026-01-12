package com.gameaccount.marketplace.controller;

import com.gameaccount.marketplace.dto.request.CreateReviewRequest;
import com.gameaccount.marketplace.dto.request.UpdateReviewRequest;
import com.gameaccount.marketplace.dto.response.ReviewResponse;
import com.gameaccount.marketplace.dto.response.SellerStatsResponse;
import com.gameaccount.marketplace.dto.response.UserResponse;
import com.gameaccount.marketplace.entity.Review;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for review management.
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Creates a new review for a completed transaction.
     * Requires authentication.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long reviewerId = Long.parseLong(userDetails.getUsername());
        Review review = reviewService.createReview(
            request.getTransactionId(),
            reviewerId,
            request.getRating(),
            request.getComment()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(review));
    }

    /**
     * Gets all reviews written by a user.
     * Public endpoint - no authentication required.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews.stream().map(this::toResponse).toList());
    }

    /**
     * Gets all reviews received by a seller.
     * Public endpoint - no authentication required.
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ReviewResponse>> getSellerReviews(@PathVariable Long sellerId) {
        List<Review> reviews = reviewService.getSellerReviews(sellerId);
        return ResponseEntity.ok(reviews.stream().map(this::toResponse).toList());
    }

    /**
     * Gets seller's average rating and total review count.
     * Public endpoint - no authentication required.
     */
    @GetMapping("/seller/{sellerId}/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SellerStatsResponse> getSellerStats(@PathVariable Long sellerId) {
        SellerStatsResponse stats = reviewService.getSellerAverageRating(sellerId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Gets review for a specific transaction.
     * Requires authentication.
     */
    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> getTransactionReview(@PathVariable Long transactionId) {
        Review review = reviewService.getTransactionReview(transactionId);
        return ResponseEntity.ok(toResponse(review));
    }

    /**
     * Updates an existing review.
     * Requires authentication.
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long requesterId = Long.parseLong(userDetails.getUsername());
        Review review = reviewService.updateReview(
            id,
            requesterId,
            request.getRating(),
            request.getComment()
        );

        return ResponseEntity.ok(toResponse(review));
    }

    /**
     * Deletes a review.
     * Requires authentication.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long requesterId = Long.parseLong(userDetails.getUsername());
        reviewService.deleteReview(id, requesterId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Converts Review entity to ReviewResponse DTO.
     */
    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
            .id(review.getId())
            .reviewer(toUserResponse(review.getReviewer()))
            .targetUser(toUserResponse(review.getTargetUser()))
            .rating(review.getRating())
            .comment(review.getComment())
            .createdAt(review.getCreatedAt())
            .build();
    }

    /**
     * Converts User entity to UserResponse DTO.
     */
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .avatar(user.getAvatar())
            .role(user.getRole())
            .status(user.getStatus())
            .rating(user.getRating())
            .totalReviews(user.getTotalReviews())
            .build();
    }
}
