package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts",
    indexes = {
        @Index(name = "idx_account_seller", columnList = "seller_id"),
        @Index(name = "idx_account_game", columnList = "game_id"),
        @Index(name = "idx_account_status", columnList = "status"),
        @Index(name = "idx_account_price", columnList = "price"),
        @Index(name = "idx_account_featured", columnList = "is_featured"),
        @Index(name = "idx_account_level", columnList = "level"),
        @Index(name = "idx_account_created_at", columnList = "created_at"),
        @Index(name = "idx_account_status_featured", columnList = "status, is_featured"),
        @Index(name = "idx_account_title", columnList = "title")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", insertable = false, updatable = false)
    private Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "game_id", insertable = false, updatable = false)
    private Long gameId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column
    private Integer level;

    @Column(name = "player_rank", length = 50)
    private String rank;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.PENDING;

    @Builder.Default
    private Integer viewsCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isFeatured = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Images stored as separate table via @ElementCollection
    @ElementCollection
    @CollectionTable(name = "account_images",
        joinColumns = @JoinColumn(name = "account_id")
    )
    @Column(name = "url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    // Encrypted game credentials for the buyer
    @Column(name = "encrypted_username", length = 500)
    private String encryptedUsername;

    @Column(name = "encrypted_password", length = 500)
    private String encryptedPassword;

    public enum AccountStatus {
        PENDING, APPROVED, REJECTED, SOLD
    }
}
