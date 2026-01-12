package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.dto.response.SellerStatsResponse;
import com.gameaccount.marketplace.entity.Review;
import com.gameaccount.marketplace.entity.Transaction;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.ReviewRepository;
import com.gameaccount.marketplace.repository.TransactionRepository;
import com.gameaccount.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for review management and seller reputation tracking.
 * Handles review creation, updates, deletion, and seller rating calculations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new review for a completed transaction.
     * Updates seller's rating and totalReviews atomically.
     */
    @Transactional
    public Review createReview(Long transactionId, Long reviewerId, Integer rating, String comment) {
        log.info("Creating review for transactionId: {} by reviewerId: {}", transactionId, reviewerId);

        // 1. Validate transaction exists and is COMPLETED
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getStatus().equals(Transaction.TransactionStatus.COMPLETED)) {
            throw new BusinessException("Can only review completed transactions");
        }

        // 2. Validate reviewer is the buyer
        if (!transaction.getBuyer().getId().equals(reviewerId)) {
            throw new BusinessException("Only the buyer can review this transaction");
        }

        // 3. Validate seller exists and prevent self-review
        User seller = transaction.getSeller();
        if (seller == null) {
            throw new ResourceNotFoundException("Seller not found for this transaction");
        }
        if (seller.getId().equals(reviewerId)) {
            throw new BusinessException("You cannot review yourself");
        }

        // 4. Validate no duplicate review (reviewer -> targetUser/seller)
        if (reviewRepository.existsByReviewerIdAndTargetUserId(reviewerId, seller.getId())) {
            throw new BusinessException("You have already reviewed this seller");
        }

        // 5. Validate rating range
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException("Rating must be between 1 and 5");
        }

        // Trim comment to remove leading/trailing whitespace
        String trimmedComment = comment != null ? comment.trim() : "";

        // 6. Create and save review
        User reviewer = userRepository.findById(reviewerId)
            .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));

        Review review = Review.builder()
            .reviewer(reviewer)
            .targetUser(seller)
            .rating(rating)
            .comment(trimmedComment)
            .build();

        Review savedReview = reviewRepository.save(review);

        // 7. Update seller rating atomically
        updateUserRating(seller, rating, true);

        log.info("Review created successfully with ID: {}", savedReview.getId());
        return savedReview;
    }

    /**
     * Updates an existing review and recalculates seller rating.
     */
    @Transactional
    public Review updateReview(Long reviewId, Long requesterId, Integer newRating, String newComment) {
        log.info("Updating review ID: {} by requesterId: {}", reviewId, requesterId);

        // 1. Validate rating range
        if (newRating == null || newRating < 1 || newRating > 5) {
            throw new BusinessException("Rating must be between 1 and 5");
        }

        // Trim comment to remove leading/trailing whitespace
        String trimmedComment = newComment != null ? newComment.trim() : "";

        // 2. Load and validate ownership
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getReviewer().getId().equals(requesterId)) {
            throw new BusinessException("You can only edit your own reviews");
        }

        // 3. Store old rating for recalculation
        Integer oldRating = review.getRating();

        // 4. Update review
        review.setRating(newRating);
        review.setComment(trimmedComment);
        Review updatedReview = reviewRepository.save(review);

        // 5. Recalculate seller rating
        User seller = review.getTargetUser();
        int totalReviews = seller.getTotalReviews() != null ? seller.getTotalReviews() : 0;

        // Prevent division by zero
        if (totalReviews <= 0) {
            log.warn("Seller has invalid totalReviews count: {}", totalReviews);
            throw new BusinessException("Invalid seller review count");
        }

        double currentRating = seller.getRating() != null ? seller.getRating() : 0.0;

        // Formula: ((old x total) - old + new) / total
        double newSellerRating = ((currentRating * totalReviews) - oldRating + newRating) / totalReviews;
        seller.setRating(newSellerRating);

        userRepository.save(seller);

        log.info("Review updated successfully");
        return updatedReview;
    }

    /**
     * Deletes a review and recalculates seller rating.
     */
    @Transactional
    public void deleteReview(Long reviewId, Long requesterId) {
        log.info("Deleting review ID: {} by requesterId: {}", reviewId, requesterId);

        // 1. Load and validate ownership/admin
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getReviewer().getId().equals(requesterId)) {
            throw new BusinessException("You can only delete your own reviews");
        }

        // 2. Store data before deletion
        Integer deletedRating = review.getRating();
        User seller = review.getTargetUser();
        int totalReviews = seller.getTotalReviews();

        // 3. Delete review
        reviewRepository.delete(review);

        // 4. Recalculate seller rating
        if (totalReviews > 1) {
            double currentRating = seller.getRating();
            // Formula: ((old x total) - deleted) / (total - 1)
            double newSellerRating = ((currentRating * totalReviews) - deletedRating) / (totalReviews - 1);
            seller.setRating(newSellerRating);
            seller.setTotalReviews(totalReviews - 1);
        } else {
            seller.setRating(0.0);
            seller.setTotalReviews(0);
        }

        userRepository.save(seller);

        log.info("Review deleted successfully");
    }

    /**
     * Gets all reviews written by a user.
     */
    public List<Review> getUserReviews(Long userId) {
        log.debug("Fetching reviews for user: {}", userId);
        return reviewRepository.findByReviewerId(userId);
    }

    /**
     * Gets all reviews received by a seller.
     */
    public List<Review> getSellerReviews(Long sellerId) {
        log.debug("Fetching reviews for seller: {}", sellerId);
        return reviewRepository.findByTargetUserId(sellerId);
    }

    /**
     * Gets seller's average rating and total review count.
     */
    public SellerStatsResponse getSellerAverageRating(Long sellerId) {
        log.debug("Fetching average rating for seller: {}", sellerId);

        Double avgRating = reviewRepository.calculateAverageRating(sellerId);
        Long totalReviews = reviewRepository.countReviewsByTargetUserId(sellerId);

        return SellerStatsResponse.builder()
            .averageRating(avgRating != null ? avgRating : 0.0)
            .totalReviews(totalReviews != null ? totalReviews : 0L)
            .build();
    }

    /**
     * Gets review for a specific transaction.
     */
    public Review getTransactionReview(Long transactionId) {
        log.debug("Fetching review for transaction: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        User buyer = transaction.getBuyer();
        User seller = transaction.getSeller();

        return reviewRepository.findByReviewerIdAndTargetUserId(buyer.getId(), seller.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Review not found for this transaction"));
    }

    /**
     * Helper method to update user rating after review creation/deletion.
     */
    private void updateUserRating(User user, Integer newRating, boolean isIncrement) {
        double currentRating = user.getRating() != null ? user.getRating() : 0.0;
        int currentTotal = user.getTotalReviews() != null ? user.getTotalReviews() : 0;

        if (isIncrement) {
            // Adding new review
            double newAverageRating = ((currentRating * currentTotal) + newRating) / (currentTotal + 1);
            user.setRating(newAverageRating);
            user.setTotalReviews(currentTotal + 1);
        } else {
            // Removing review (already handled in deleteReview, kept for reference)
            if (currentTotal > 1) {
                double newAverageRating = ((currentRating * currentTotal) - newRating) / (currentTotal - 1);
                user.setRating(newAverageRating);
                user.setTotalReviews(currentTotal - 1);
            } else {
                user.setRating(0.0);
                user.setTotalReviews(0);
            }
        }

        userRepository.save(user);
    }
}
