package com.gameaccount.marketplace.controller;

import com.gameaccount.marketplace.dto.request.PurchaseRequest;
import com.gameaccount.marketplace.dto.response.CredentialsResponse;
import com.gameaccount.marketplace.dto.response.PurchaseResponse;
import com.gameaccount.marketplace.dto.response.TransactionResponse;
import com.gameaccount.marketplace.entity.Transaction;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.service.PayOSService;
import com.gameaccount.marketplace.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for transaction management.
 * Handles purchase flow, transaction queries, and completion/cancellation.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${frontend.url}")
public class TransactionController {

    private final TransactionService transactionService;
    private final PayOSService payOSService;

    /**
     * Purchase a game account.
     * Creates transaction and returns payment URL.
     *
     * @param request Purchase request with account ID and credentials
     * @param userDetails Authenticated user details
     * @return PurchaseResponse with transaction ID and checkout URL
     */
    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponse> purchaseAccount(
            @Valid @RequestBody PurchaseRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Long buyerId = getUserIdFromUserDetails(userDetails);
        log.info("Purchase request: accountId={}, buyerId={}", request.getAccountId(), buyerId);

        // Purchase account (creates PENDING transaction)
        Transaction transaction = transactionService.purchaseAccount(
            request.getAccountId(),
            buyerId,
            request.getCredentials()
        );

        // Create payment link
        var paymentLink = payOSService.createPaymentLink(transaction.getId());

        PurchaseResponse response = PurchaseResponse.builder()
            .transactionId(transaction.getId())
            .checkoutUrl(paymentLink.getCheckoutUrl())
            .amount(transaction.getAmount())
            .orderCode(paymentLink.getOrderCode())
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get current user's transactions (as buyer or seller).
     *
     * @param userDetails Authenticated user details
     * @return List of user's transactions
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        log.debug("Fetching transactions for user: userId={}", userId);

        // Get transactions as buyer and seller
        List<Transaction> buyerTransactions = transactionService.getMyTransactions(userId);
        List<Transaction> sellerTransactions = transactionService.getSellerTransactions(userId);

        // Combine and deduplicate using LinkedHashSet
        Set<Transaction> allTransactions = new LinkedHashSet<>();
        allTransactions.addAll(buyerTransactions);
        allTransactions.addAll(sellerTransactions);

        // Convert to response DTOs and sort by createdAt descending
        List<TransactionResponse> responses = allTransactions.stream()
            .map(this::mapToResponse)
            .sorted(Comparator.comparing(TransactionResponse::getCreatedAt).reversed())
            .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get transaction by ID.
     * Only accessible by transaction participants (buyer, seller) or admin.
     *
     * @param id Transaction ID
     * @param userDetails Authenticated user details
     * @return Transaction details
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        boolean isAdmin = hasRole(userDetails, "ADMIN");
        log.debug("Fetching transaction: id={}, userId={}, isAdmin={}", id, userId, isAdmin);

        Transaction transaction = transactionService.getTransaction(id);

        // Check ownership: user must be buyer, seller, or admin
        if (!transaction.getBuyerId().equals(userId)
            && !transaction.getSellerId().equals(userId)
            && !isAdmin) {
            log.warn("Unauthorized access attempt: transactionId={}, userId={}", id, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(mapToResponse(transaction));
    }

    /**
     * Complete a transaction.
     * Restricted to admin or seller.
     *
     * @param id Transaction ID
     * @param userDetails Authenticated user details
     * @return CredentialsResponse with decrypted credentials
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<CredentialsResponse> completeTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Long requesterId = getUserIdFromUserDetails(userDetails);
        boolean isAdmin = hasRole(userDetails, "ADMIN");

        // Fetch transaction to verify seller authorization
        Transaction transaction = transactionService.getTransaction(id);

        // Check if user is admin OR seller
        if (!isAdmin && !transaction.getSellerId().equals(requesterId)) {
            log.warn("Unauthorized complete attempt: transactionId={}, userId={}", id, requesterId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Completing transaction: id={}, requesterId={}", id, requesterId);

        CredentialsResponse response = transactionService.completeTransaction(id, requesterId);

        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a transaction.
     * Restricted to admin or buyer.
     *
     * @param id Transaction ID
     * @param userDetails Authenticated user details
     * @return HTTP 204 no content on success
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        Long requesterId = getUserIdFromUserDetails(userDetails);
        boolean isAdmin = hasRole(userDetails, "ADMIN");

        // Fetch transaction to verify buyer authorization
        Transaction transaction = transactionService.getTransaction(id);

        // Check if user is admin OR buyer
        if (!isAdmin && !transaction.getBuyerId().equals(requesterId)) {
            log.warn("Unauthorized cancel attempt: transactionId={}, userId={}", id, requesterId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Cancelling transaction: id={}, requesterId={}", id, requesterId);

        transactionService.cancelTransaction(id, requesterId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts user ID from Spring Security UserDetails.
     * Assumes username contains the user ID as string.
     *
     * @param userDetails Spring Security UserDetails
     * @return User ID as Long
     */
    private Long getUserIdFromUserDetails(org.springframework.security.core.userdetails.User userDetails) {
        try {
            return Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            throw new BusinessException("Invalid user ID in authentication token");
        }
    }

    /**
     * Checks if user has a specific role.
     *
     * @param userDetails Spring Security UserDetails
     * @param role Role name (e.g., "ADMIN", "BUYER", "SELLER")
     * @return true if user has the role
     */
    private boolean hasRole(org.springframework.security.core.userdetails.User userDetails, String role) {
        return userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Maps Transaction entity to TransactionResponse DTO.
     * Handles lazy loading of related entities.
     *
     * @param transaction Transaction entity
     * @return TransactionResponse DTO
     */
    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
            .id(transaction.getId())
            .amount(transaction.getAmount())
            .status(transaction.getStatus().name())
            .createdAt(transaction.getCreatedAt())
            .completedAt(transaction.getCompletedAt())
            .orderCode(transaction.getOrderCode())
            .accountId(transaction.getAccountId())
            .accountTitle(getAccountTitle(transaction))
            .accountPrice(transaction.getAmount())
            .buyerId(transaction.getBuyerId())
            .buyerName(getUserName(transaction.getBuyer()))
            .sellerId(transaction.getSellerId())
            .sellerName(getUserName(transaction.getSeller()))
            .build();
    }

    /**
     * Safely gets account title from transaction.
     * Handles potential lazy loading issues.
     */
    private String getAccountTitle(Transaction transaction) {
        try {
            return transaction.getAccount() != null ? transaction.getAccount().getTitle() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Safely gets user full name.
     * Handles potential lazy loading issues.
     */
    private String getUserName(User user) {
        try {
            return user != null ? user.getFullName() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
