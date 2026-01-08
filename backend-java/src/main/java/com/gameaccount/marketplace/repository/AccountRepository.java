package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    List<Account> findBySellerId(Long sellerId);

    Page<Account> findBySellerId(Long sellerId, Pageable pageable);

    Page<Account> findBySellerIdAndStatus(Long sellerId, AccountStatus status, Pageable pageable);

    List<Account> findByGameId(Long gameId);

    List<Account> findByStatus(AccountStatus status);

    List<Account> findByStatusAndIsFeatured(AccountStatus status, boolean isFeatured);

    @Query("SELECT a FROM Account a WHERE a.status = :status ORDER BY a.viewsCount DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Account> findPopularAccounts(@Param("status") AccountStatus status);

    @Query("SELECT a FROM Account a WHERE a.status = 'PENDING'")
    List<Account> findPendingAccounts();

    // For search functionality (Story 3.1)
    @Query("SELECT a FROM Account a WHERE " +
           "(:gameId IS NULL OR a.game.id = :gameId) AND " +
           "(:minPrice IS NULL OR a.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR a.price <= :maxPrice) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Account> searchAccounts(
        @Param("gameId") Long gameId,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("status") AccountStatus status,
        Pageable pageable
    );
}
