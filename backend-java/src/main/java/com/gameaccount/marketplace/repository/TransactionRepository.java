package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.Transaction;
import com.gameaccount.marketplace.entity.Transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBuyerId(Long buyerId);

    List<Transaction> findBySellerId(Long sellerId);

    List<Transaction> findByAccountId(Long accountId);

    Optional<Transaction> findByAccount_IdAndBuyer_Id(Long accountId, Long buyerId);

    List<Transaction> findByStatus(TransactionStatus status);

    boolean existsByBuyerIdAndAccountId(Long buyerId, Long accountId);

    // PayOS integration methods
    Optional<Transaction> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);
}
