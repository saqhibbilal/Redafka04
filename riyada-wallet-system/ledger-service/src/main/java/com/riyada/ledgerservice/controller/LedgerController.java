package com.riyada.ledgerservice.controller;

import com.riyada.ledgerservice.dto.RecordTransactionRequest;
import com.riyada.ledgerservice.dto.TransactionDTO;
import com.riyada.ledgerservice.dto.AuditLogDTO;
import com.riyada.ledgerservice.service.LedgerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/ledger")
@CrossOrigin(origins = "*")
public class LedgerController {

    private static final Logger logger = LoggerFactory.getLogger(LedgerController.class);

    @Autowired
    private LedgerService ledgerService;

    /**
     * Record a new transaction
     */
    @PostMapping("/record-transaction")
    public ResponseEntity<?> recordTransaction(@Valid @RequestBody RecordTransactionRequest request) {
        try {
            logger.info("Received request to record transaction for payment ID: {}", request.getPaymentId());

            TransactionDTO transaction = ledgerService.recordTransaction(request);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Transaction recorded successfully", transaction));
        } catch (Exception e) {
            logger.error("Failed to record transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to record transaction: " + e.getMessage(), null));
        }
    }

    /**
     * Update transaction status
     */
    @PutMapping("/transactions/{transactionId}/status")
    public ResponseEntity<?> updateTransactionStatus(
            @PathVariable UUID transactionId,
            @RequestParam String status,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            logger.info("Updating transaction {} status to {} by user {}", transactionId, status, userId);

            TransactionDTO transaction = ledgerService.updateTransactionStatus(transactionId, status, userId);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Transaction status updated successfully", transaction));
        } catch (Exception e) {
            logger.error("Failed to update transaction status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to update transaction status: " + e.getMessage(), null));
        }
    }

    /**
     * Get transactions for the authenticated user
     */
    @GetMapping("/transactions")
    public ResponseEntity<?> getUserTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            logger.info("Fetching transactions for user: {}, page: {}, size: {}", userId, page, size);

            Page<TransactionDTO> transactions = ledgerService.getUserTransactions(userId, page, size);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            logger.error("Failed to fetch user transactions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to fetch transactions: " + e.getMessage(), null));
        }
    }

    /**
     * Get transaction by ID
     */
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable UUID transactionId) {
        try {
            logger.info("Fetching transaction by ID: {}", transactionId);

            Optional<TransactionDTO> transaction = ledgerService.getTransactionById(transactionId);

            if (transaction.isPresent()) {
                return ResponseEntity.ok()
                        .body(new ApiResponse<>(true, "Transaction retrieved successfully", transaction.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Transaction not found", null));
            }
        } catch (Exception e) {
            logger.error("Failed to fetch transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to fetch transaction: " + e.getMessage(), null));
        }
    }

    /**
     * Get transactions by payment ID
     */
    @GetMapping("/transactions/payment/{paymentId}")
    public ResponseEntity<?> getTransactionsByPaymentId(@PathVariable UUID paymentId) {
        try {
            logger.info("Fetching transactions by payment ID: {}", paymentId);

            List<TransactionDTO> transactions = ledgerService.getTransactionsByPaymentId(paymentId);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            logger.error("Failed to fetch transactions by payment ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to fetch transactions: " + e.getMessage(), null));
        }
    }

    /**
     * Get recent transactions for the authenticated user
     */
    @GetMapping("/transactions/recent")
    public ResponseEntity<?> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            logger.info("Fetching recent transactions for user: {}, limit: {}", userId, limit);

            List<TransactionDTO> transactions = ledgerService.getRecentTransactions(userId, limit);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Recent transactions retrieved successfully", transactions));
        } catch (Exception e) {
            logger.error("Failed to fetch recent transactions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to fetch recent transactions: " + e.getMessage(), null));
        }
    }

    /**
     * Get audit trail for a transaction
     */
    @GetMapping("/transactions/{transactionId}/audit-trail")
    public ResponseEntity<?> getAuditTrail(@PathVariable UUID transactionId) {
        try {
            logger.info("Fetching audit trail for transaction ID: {}", transactionId);

            List<AuditLogDTO> auditLogs = ledgerService.getAuditTrail(transactionId);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Audit trail retrieved successfully", auditLogs));
        } catch (Exception e) {
            logger.error("Failed to fetch audit trail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to fetch audit trail: " + e.getMessage(), null));
        }
    }

    /**
     * Get financial summary for the authenticated user
     */
    @GetMapping("/reports/summary")
    public ResponseEntity<?> getFinancialSummary(Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            logger.info("Generating financial summary for user: {}", userId);

            LedgerService.FinancialSummaryDTO summary = ledgerService.getFinancialSummary(userId);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Financial summary retrieved successfully", summary));
        } catch (Exception e) {
            logger.error("Failed to generate financial summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to generate financial summary: " + e.getMessage(), null));
        }
    }

    /**
     * Search transactions with filters
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            logger.info("Searching transactions for user: {}", userId);

            Page<TransactionDTO> transactions = ledgerService.searchTransactions(
                    userId, status, transactionType, startDate, endDate, page, size);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Search results retrieved successfully", transactions));
        } catch (Exception e) {
            logger.error("Failed to search transactions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to search transactions: " + e.getMessage(), null));
        }
    }

    /**
     * Generic API Response class
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters
        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }
    }
}
