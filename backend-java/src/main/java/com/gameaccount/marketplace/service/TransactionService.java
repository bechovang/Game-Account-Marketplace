package com.gameaccount.marketplace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameaccount.marketplace.dto.response.CredentialsResponse;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.entity.Transaction;
import com.gameaccount.marketplace.entity.Transaction.TransactionStatus;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.entity.User.Role;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.AccountRepository;
import com.gameaccount.marketplace.repository.TransactionRepository;
import com.gameaccount.marketplace.repository.UserRepository;
import com.gameaccount.marketplace.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for transaction management and credential security.
 * Handles purchase flow, credential encryption/decryption, and transaction lifecycle.
 *
 * <p>Transaction Flow:
 * <ol>
 *   <li>purchaseAccount() - Creates PENDING transaction with encrypted credentials</li>
 *   <li>Payment gateway processes payment (Story 4.3)</li>
 *   <li>completeTransaction() - Releases decrypted credentials to buyer</li>
 *   <li>cancelTransaction() - Cancels transaction without releasing credentials</li>
 * </ol>
 *
 * @see <a href="https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html">Spring @Transactional</a>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Pending transactions expire after 5 minutes.
     * This allows retry if payment fails or user abandons checkout.
     */
    private static final int TRANSACTION_TIMEOUT_MINUTES = 5;

    /**
     * Creates a new purchase transaction for an account.
     * Validates account availability and stores transaction with PENDING status.
     * Credentials are stored in the Account entity, not in Transaction.
     *
     * @param accountId The ID of the account being purchased
     * @param buyerId The ID of the user making the purchase
     * @return The created Transaction entity
     * @throws ResourceNotFoundException if account or buyer not found
     * @throws BusinessException if account not available or buyer is seller
     */
    @Transactional
    public Transaction purchaseAccount(Long accountId, Long buyerId) {
        log.info("Initiating purchase: accountId={}, buyerId={}", accountId, buyerId);

        // Validate account exists
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Validate buyer exists
        if (!userRepository.existsById(buyerId)) {
            throw new ResourceNotFoundException("User not found with id: " + buyerId);
        }

        // Validate account status is APPROVED
        if (account.getStatus() != AccountStatus.APPROVED) {
            log.warn("Purchase attempt on non-approved account: status={}", account.getStatus());
            throw new BusinessException("Account is not available for purchase. Current status: " + account.getStatus());
        }

        // Validate buyer is not the seller
        if (account.getSellerId().equals(buyerId)) {
            log.warn("Buyer attempted to purchase their own account: buyerId={}", buyerId);
            throw new BusinessException("You cannot purchase your own account");
        }

        // Auto-cancel expired pending transactions before checking for duplicates
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(TRANSACTION_TIMEOUT_MINUTES);
        List<Transaction> expiredTransactions = transactionRepository.findPendingOlderThan(
            buyerId, accountId, expirationTime);

        if (!expiredTransactions.isEmpty()) {
            log.info("Auto-cancelling {} expired pending transaction(s) for buyerId={}, accountId={}",
                expiredTransactions.size(), buyerId, accountId);
            for (Transaction expired : expiredTransactions) {
                expired.setStatus(TransactionStatus.CANCELLED);
                transactionRepository.save(expired);
            }
        }

        // Check for existing transaction with detailed status message
        List<Transaction> existingTransactions = transactionRepository.findByBuyerIdAndAccountId(buyerId, accountId);
        if (!existingTransactions.isEmpty()) {
            Transaction existing = existingTransactions.get(0); // Get most recent

            switch (existing.getStatus()) {
                case PENDING:
                    log.warn("Pending transaction exists: buyerId={}, accountId={}, transactionId={}",
                        buyerId, accountId, existing.getId());
                    throw new BusinessException(
                        "You have a pending purchase for this account. " +
                        "Please complete the payment or wait for it to expire (30 minutes). " +
                        "Transaction ID: " + existing.getId()
                    );

                case COMPLETED:
                    log.warn("Already purchased: buyerId={}, accountId={}, transactionId={}",
                        buyerId, accountId, existing.getId());
                    throw new BusinessException(
                        "You have already purchased this account on " +
                        existing.getCompletedAt().toLocalDate() +
                        ". Transaction ID: " + existing.getId()
                    );

                case CANCELLED:
                    // Allow new purchase if previous transaction was cancelled
                    log.info("Previous transaction cancelled, allowing new purchase: transactionId={}", existing.getId());
                    break;

                default:
                    log.warn("Unexpected transaction status: {}", existing.getStatus());
                    throw new BusinessException(
                        "Unable to process purchase. Transaction ID: " + existing.getId() +
                        " has status: " + existing.getStatus()
                    );
            }
        }

        // Create transaction (no credentials stored here - they're in Account)
        Transaction transaction = Transaction.builder()
            .account(account)
            .buyer(userRepository.getById(buyerId))
            .seller(account.getSeller())
            .amount(account.getPrice())
            .status(TransactionStatus.PENDING)
            .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created: id={}, accountId={}, buyerId={}, amount={}",
            saved.getId(), accountId, buyerId, account.getPrice());

        return saved;
    }

    /**
     * Completes a transaction and returns decrypted account credentials to the buyer.
     * Only the buyer or an admin can complete a transaction.
     * Credentials are retrieved from the Account entity, not from Transaction.
     *
     * @param transactionId The ID of the transaction to complete
     * @param requesterId The ID of the user requesting completion
     * @return CredentialsResponse containing decrypted username and password from Account
     * @throws ResourceNotFoundException if transaction not found
     * @throws BusinessException if transaction not in PENDING status or requester not authorized
     */
    @Transactional
    public CredentialsResponse completeTransaction(Long transactionId, Long requesterId) {
        log.info("Completing transaction: transactionId={}, requesterId={}", transactionId, requesterId);

        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        // Validate transaction status
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.warn("Attempt to complete non-pending transaction: status={}", transaction.getStatus());
            throw new BusinessException("Transaction cannot be completed. Current status: " + transaction.getStatus());
        }

        // Validate authorization (only buyer or admin can complete)
        if (!transaction.getBuyerId().equals(requesterId)) {
            // Check if requester is admin
            boolean isAdmin = userRepository.findById(requesterId)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);

            if (!isAdmin) {
                log.warn("Unauthorized completion attempt: requesterId={}, transactionBuyerId={}",
                    requesterId, transaction.getBuyerId());
                throw new BusinessException("You are not authorized to complete this transaction");
            }
        }

        // Get account from transaction
        Account account = transaction.getAccount();

        // Decrypt credentials from Account entity
        // If credentials are not set (demo accounts), generate placeholder credentials
        try {
            String username;
            String password;

            // Check if account has encrypted credentials
            if (account.getEncryptedUsername() != null && !account.getEncryptedUsername().isEmpty()) {
                username = encryptionUtil.decrypt(account.getEncryptedUsername());
                password = encryptionUtil.decrypt(account.getEncryptedPassword());
            } else {
                // Demo account - generate placeholder credentials
                log.warn("Account has no encrypted credentials, using placeholder for demo: accountId={}", account.getId());
                username = "demo_user_" + account.getId();
                password = "demo_pass_" + account.getId();
            }

            // Update transaction status
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            // Also mark account as sold
            account.setStatus(AccountStatus.SOLD);
            accountRepository.save(account);

            log.info("Transaction completed: id={}, buyerId={}, accountId={}", transactionId, requesterId, account.getId());

            return new CredentialsResponse(username, password);
        } catch (Exception e) {
            log.error("Failed to complete transaction", e);
            throw new BusinessException("Failed to retrieve credentials", e);
        }
    }

    /**
     * Cancels a pending transaction.
     * Only the buyer or an admin can cancel a transaction.
     *
     * @param transactionId The ID of the transaction to cancel
     * @param requesterId The ID of the user requesting cancellation
     * @throws ResourceNotFoundException if transaction not found
     * @throws BusinessException if transaction not in PENDING status or requester not authorized
     */
    @Transactional
    public void cancelTransaction(Long transactionId, Long requesterId) {
        log.info("Cancelling transaction: transactionId={}, requesterId={}", transactionId, requesterId);

        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        // Validate transaction status
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.warn("Attempt to cancel non-pending transaction: status={}", transaction.getStatus());
            throw new BusinessException("Transaction cannot be cancelled. Current status: " + transaction.getStatus());
        }

        // Validate authorization (only buyer or admin can cancel)
        if (!transaction.getBuyerId().equals(requesterId)) {
            // Check if requester is admin
            boolean isAdmin = userRepository.findById(requesterId)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);

            if (!isAdmin) {
                log.warn("Unauthorized cancellation attempt: requesterId={}, transactionBuyerId={}",
                    requesterId, transaction.getBuyerId());
                throw new BusinessException("You are not authorized to cancel this transaction");
            }
        }

        // Update transaction status
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);

        log.info("Transaction cancelled: id={}, requesterId={}", transactionId, requesterId);
    }

    /**
     * Returns all transactions for a specific buyer.
     *
     * @param buyerId The ID of the buyer
     * @return List of transactions ordered by creation date (most recent first)
     */
    public List<Transaction> getMyTransactions(Long buyerId) {
        log.debug("Fetching transactions for buyer: buyerId={}", buyerId);
        return transactionRepository.findByBuyerId(buyerId);
    }

    /**
     * Returns all transactions for a specific seller.
     *
     * @param sellerId The ID of the seller
     * @return List of transactions ordered by creation date (most recent first)
     */
    public List<Transaction> getSellerTransactions(Long sellerId) {
        log.debug("Fetching transactions for seller: sellerId={}", sellerId);
        return transactionRepository.findBySellerId(sellerId);
    }

    /**
     * Gets a transaction by ID.
     *
     * @param transactionId The ID of the transaction
     * @return The Transaction entity
     * @throws ResourceNotFoundException if transaction not found
     */
    public Transaction getTransaction(Long transactionId) {
        log.debug("Fetching transaction: transactionId={}", transactionId);
        return transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
    }

    /**
     * Completes transaction by PayOS order code.
     * Called by webhook when payment is successful.
     * Idempotent: returns actual credentials even for already-completed transactions.
     *
     * @param orderCode The PayOS order code
     * @return CredentialsResponse containing decrypted credentials
     * @throws ResourceNotFoundException if transaction not found
     */
    @Transactional
    public CredentialsResponse completeTransactionByOrderCode(String orderCode) {
        log.info("Completing transaction by order code: orderCode={}", orderCode);

        Transaction transaction = transactionRepository.findByOrderCode(orderCode)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with order code: " + orderCode));

        // Check if already completed (idempotency)
        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            log.info("Transaction already completed, returning cached credentials: orderCode={}", orderCode);
            // Return actual decrypted credentials for already-completed transactions
            return getCredentialsForCompletedTransaction(transaction);
        }

        return completeTransaction(transaction.getId(), transaction.getBuyerId());
    }

    /**
     * Retrieves decrypted credentials for an already-completed transaction.
     * Used for idempotent webhook responses.
     *
     * @param transaction The completed transaction
     * @return CredentialsResponse containing decrypted credentials
     */
    private CredentialsResponse getCredentialsForCompletedTransaction(Transaction transaction) {
        try {
            String decryptedCredentials = encryptionUtil.decrypt(transaction.getEncryptedCredentials());
            JsonNode credentialsJson = objectMapper.readTree(decryptedCredentials);

            String username = credentialsJson.get("username").asText();
            String password = credentialsJson.get("password").asText();

            return new CredentialsResponse(username, password);
        } catch (Exception e) {
            log.error("Failed to retrieve credentials for completed transaction: {}", transaction.getId(), e);
            throw new BusinessException("Failed to retrieve credentials", e);
        }
    }

    /**
     * Cancels transaction by PayOS order code.
     * Called by webhook when payment is cancelled or expired.
     *
     * @param orderCode The PayOS order code
     * @throws ResourceNotFoundException if transaction not found
     */
    @Transactional
    public void cancelTransactionByOrderCode(String orderCode) {
        log.info("Cancelling transaction by order code: orderCode={}", orderCode);

        Transaction transaction = transactionRepository.findByOrderCode(orderCode)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with order code: " + orderCode));

        // Check if already cancelled (idempotency)
        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            log.warn("Transaction already cancelled: orderCode={}", orderCode);
            return;
        }

        cancelTransaction(transaction.getId(), transaction.getBuyerId());
    }
}
