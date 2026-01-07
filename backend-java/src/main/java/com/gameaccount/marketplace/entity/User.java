package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 100)
    private String fullName;

    @Column(length = 255)
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.BUYER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column
    @Builder.Default
    private Double balance = 0.0;

    @Column
    @Builder.Default
    private Double rating = 0.0;

    @Builder.Default
    private Integer totalReviews = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relationships for future stories (Account, Transaction entities)
    // These will be added in Epic 2: Account Listing Management
    // @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Account> accounts;

    // These will be added in Epic 4: Secure Transactions
    // @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Transaction> purchases;

    public enum Role {
        BUYER, SELLER, ADMIN
    }

    public enum UserStatus {
        ACTIVE, BANNED, SUSPENDED
    }
}
