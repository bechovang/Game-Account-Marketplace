package com.gameaccount.marketplace.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transaction entity representing a purchase transaction for a game account.
 * Stores encrypted account credentials that are released to buyer upon successful payment.
 * Tracks transaction status through PENDING → COMPLETED/CANCELLED lifecycle.
 */
@Entity
@Table(name = "transactions",
    indexes = {
        @Index(name = "idx_transaction_buyer", columnList = "buyer_id"),
        @Index(name = "idx_transaction_seller", columnList = "seller_id"),
        @Index(name = "idx_transaction_account", columnList = "account_id"),
        @Index(name = "idx_transaction_status", columnList = "status"),
        @Index(name = "idx_transaction_order_code", columnList = "order_code", unique = true)
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", insertable = false, updatable = false)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    private Account account;

    @Column(name = "buyer_id", insertable = false, updatable = false)
    private Long buyerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @ToString.Exclude
    private User buyer;

    @Column(name = "seller_id", insertable = false, updatable = false)
    private Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @ToString.Exclude
    private User seller;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Lob
    @Column
    @JsonIgnore
    private String encryptedCredentials;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime completedAt;

    @Column(name = "order_code", length = 100, unique = true)
    private String orderCode; // PayOS order code for payment reconciliation

    public enum TransactionStatus {
        PENDING, COMPLETED, CANCELLED
    }

    /**
     * Business key equality: buyer purchasing account.
     * Prevents duplicate transactions for same account+buyer pair.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(accountId, that.accountId) &&
               Objects.equals(buyerId, that.buyerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, buyerId);
    }
}
