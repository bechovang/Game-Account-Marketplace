package com.gameaccount.marketplace.scheduler;

import com.gameaccount.marketplace.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Scheduled job to clean up expired pending transactions.
 * Runs every hour to cancel transactions older than 30 minutes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionCleanupScheduler {

    private final TransactionRepository transactionRepository;

    /**
     * Cancels expired pending transactions every hour.
     * Transactions older than 30 minutes are automatically cancelled.
     */
    @Scheduled(fixedRate = 3600000) // Every hour (in milliseconds)
    @Transactional
    public void cleanupExpiredTransactions() {
        log.info("Starting cleanup of expired pending transactions...");

        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(30);

        int cancelledCount = transactionRepository.cancelExpiredPendingTransactions(expirationTime);

        if (cancelledCount > 0) {
            log.info("Cleanup complete: {} expired transaction(s) cancelled", cancelledCount);
        } else {
            log.debug("Cleanup complete: no expired transactions found");
        }
    }
}
