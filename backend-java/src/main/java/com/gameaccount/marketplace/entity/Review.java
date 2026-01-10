package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Review entity representing a user's review of another user.
 * Enforces one-to-one relationship: each reviewer can review each target user only once.
 */
@Entity
@Table(name = "reviews",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"reviewer_id", "target_user_id"})
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewer_id", insertable = false, updatable = false)
    private Long reviewerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    @ToString.Exclude
    private User reviewer;

    @Column(name = "target_user_id", insertable = false, updatable = false)
    private Long targetUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    @ToString.Exclude
    private User targetUser;

    @Column(nullable = false)
    @Min(1) @Max(5)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Business key equality: reviewer reviewing targetUser.
     * Enforces one review per reviewer-target pair.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(reviewerId, review.reviewerId) &&
               Objects.equals(targetUserId, review.targetUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewerId, targetUserId);
    }
}
