package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTargetUserId(Long targetUserId);

    List<Review> findByReviewerId(Long reviewerId);

    boolean existsByReviewerIdAndTargetUserId(Long reviewerId, Long targetUserId);

    Optional<Review> findByReviewerIdAndTargetUserId(Long reviewerId, Long targetUserId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetUser.id = :targetUserId")
    Double calculateAverageRating(@Param("targetUserId") Long targetUserId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.targetUser.id = :targetUserId")
    Long countReviewsByTargetUserId(@Param("targetUserId") Long targetUserId);
}
