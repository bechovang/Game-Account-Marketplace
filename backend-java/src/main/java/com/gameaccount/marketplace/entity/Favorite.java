package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing a user's favorite account.
 * This is a join table entity with additional fields (createdAt).
 *
 * Database Schema Notes:
 * - ON DELETE CASCADE on account_id: When an Account is deleted, all Favorite entries
 *   referencing it are automatically removed. This prevents orphaned favorite records.
 * - This is enforced at database level via foreign key constraint.
 */
@Entity
@Table(name = "favorites",
    uniqueConstraints = {
        @UniqueConstraint(name = "idx_favorite_user_account",
                         columnNames = {"user_id", "account_id"})
    },
    indexes = {
        @Index(name = "idx_favorite_user", columnList = "user_id"),
        @Index(name = "idx_favorite_account", columnList = "account_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
