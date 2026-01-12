package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.Transaction;
import com.gameaccount.marketplace.entity.Transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBuyerId(Long buyerId);

    List<Transaction> findBySellerId(Long sellerId);

    List<Transaction> findByAccountId(Long accountId);

    /**
     * Finds all transactions for a specific buyer and account.
     * Used for duplicate purchase checking.
     */
    List<Transaction> findByBuyerIdAndAccountId(Long buyerId, Long accountId);

    Optional<Transaction> findByAccount_IdAndBuyer_Id(Long accountId, Long buyerId);

    List<Transaction> findByStatus(TransactionStatus status);

    boolean existsByBuyerIdAndAccountId(Long buyerId, Long accountId);

    // PayOS integration methods
    Optional<Transaction> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);

    /**
     * Finds pending transactions older than specified minutes that haven't been paid.
     * Used for cleanup and to allow retry after expiration.
     */
    @Query("SELECT t FROM Transaction t WHERE t.buyerId = :buyerId AND t.account.id = :accountId " +
           "AND t.status = 'PENDING' AND t.createdAt < :expirationTime")
    List<Transaction> findPendingOlderThan(@Param("buyerId") Long buyerId,
                                           @Param("accountId") Long accountId,
                                           @Param("expirationTime") LocalDateTime expirationTime);

    /**
     * Cancels expired pending transactions (sets status to CANCELLED).
     * Used for automatic cleanup.
     */
    @Modifying
    @Query("UPDATE Transaction t SET t.status = 'CANCELLED' WHERE t.status = 'PENDING' " +
           "AND t.createdAt < :expirationTime")
    int cancelExpiredPendingTransactions(@Param("expirationTime") LocalDateTime expirationTime);

    /**
     * Finds a transaction by ID with account, buyer, and seller eagerly fetched.
     * Used for demo auto-completion to avoid LazyInitializationException.
     */
    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.account LEFT JOIN FETCH t.buyer LEFT JOIN FETCH t.seller WHERE t.id = :id")
    Optional<Transaction> findByIdWithAccount(@Param("id") Long id);
}
