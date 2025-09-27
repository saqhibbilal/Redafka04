package com.riyada.ledgerservice.service;

import com.riyada.ledgerservice.dto.RecordTransactionRequest;
import com.riyada.ledgerservice.dto.TransactionDTO;
import com.riyada.ledgerservice.dto.AuditLogDTO;
import com.riyada.ledgerservice.entity.AuditLog;
import com.riyada.ledgerservice.entity.Transaction;
import com.riyada.ledgerservice.repository.AuditLogRepository;
import com.riyada.ledgerservice.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LedgerService {

    private static final Logger logger = LoggerFactory.getLogger(LedgerService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Record a new transaction in the ledger
     */
    public TransactionDTO recordTransaction(RecordTransactionRequest request) {
        logger.info("Recording transaction for payment ID: {}", request.getPaymentId());

        // Create transaction entity
        Transaction transaction = new Transaction();
        transaction.setPaymentId(request.getPaymentId());
        transaction.setSenderUserId(request.getSenderUserId());
        transaction.setReceiverUserId(request.getReceiverUserId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionType(Transaction.TransactionType.valueOf(request.getTransactionType()));
        transaction.setStatus(Transaction.TransactionStatus.valueOf(request.getStatus()));
        transaction.setDescription(request.getDescription());

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Create audit log for transaction creation
        createAuditLog(savedTransaction, "TRANSACTION_CREATED", null,
                convertTransactionToJson(savedTransaction), null);

        logger.info("Transaction recorded successfully with ID: {}", savedTransaction.getId());
        return convertToDTO(savedTransaction);
    }

    /**
     * Update transaction status
     */
    public TransactionDTO updateTransactionStatus(UUID transactionId, String newStatus, UUID userId) {
        logger.info("Updating transaction status for ID: {} to {}", transactionId, newStatus);

        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (transactionOpt.isEmpty()) {
            throw new RuntimeException("Transaction not found with ID: " + transactionId);
        }

        Transaction transaction = transactionOpt.get();
        Transaction.TransactionStatus oldStatus = transaction.getStatus();
        Transaction.TransactionStatus newStatusEnum = Transaction.TransactionStatus.valueOf(newStatus);

        transaction.setStatus(newStatusEnum);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Create audit log for status update
        createAuditLog(updatedTransaction, "STATUS_UPDATED",
                "{\"status\":\"" + oldStatus + "\"}",
                "{\"status\":\"" + newStatus + "\"}",
                userId);

        logger.info("Transaction status updated successfully");
        return convertToDTO(updatedTransaction);
    }

    /**
     * Get transactions for a user with pagination
     */
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getUserTransactions(UUID userId, int page, int size) {
        logger.info("Fetching transactions for user: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByUserId(userId, pageable);

        return transactions.map(this::convertToDTO);
    }

    /**
     * Get transaction by ID
     */
    @Transactional(readOnly = true)
    public Optional<TransactionDTO> getTransactionById(UUID transactionId) {
        logger.info("Fetching transaction by ID: {}", transactionId);

        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        return transaction.map(this::convertToDTO);
    }

    /**
     * Get transactions by payment ID
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByPaymentId(UUID paymentId) {
        logger.info("Fetching transactions by payment ID: {}", paymentId);

        List<Transaction> transactions = transactionRepository.findByPaymentId(paymentId);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recent transactions for a user
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getRecentTransactions(UUID userId, int limit) {
        logger.info("Fetching recent transactions for user: {}, limit: {}", userId, limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<Transaction> transactions = transactionRepository.findRecentTransactionsByUserId(userId, pageable);

        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get audit trail for a transaction
     */
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getAuditTrail(UUID transactionId) {
        logger.info("Fetching audit trail for transaction ID: {}", transactionId);

        List<AuditLog> auditLogs = auditLogRepository.findByTransactionIdOrderByCreatedAtAsc(transactionId);
        return auditLogs.stream()
                .map(this::convertAuditLogToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get financial summary for a user
     */
    @Transactional(readOnly = true)
    public FinancialSummaryDTO getFinancialSummary(UUID userId) {
        logger.info("Generating financial summary for user: {}", userId);

        Double totalSent = transactionRepository.getTotalSentByUser(userId);
        Double totalReceived = transactionRepository.getTotalReceivedByUser(userId);
        long totalTransactions = transactionRepository.countByUserId(userId);

        return new FinancialSummaryDTO(
                totalSent != null ? totalSent : 0.0,
                totalReceived != null ? totalReceived : 0.0,
                totalTransactions);
    }

    /**
     * Search transactions with filters
     */
    @Transactional(readOnly = true)
    public Page<TransactionDTO> searchTransactions(UUID userId, String status, String transactionType,
            LocalDateTime startDate, LocalDateTime endDate,
            int page, int size) {
        logger.info("Searching transactions for user: {}", userId);

        Pageable pageable = PageRequest.of(page, size);

        if (startDate != null && endDate != null) {
            return transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable)
                    .map(this::convertToDTO);
        }

        return transactionRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Create audit log entry
     */
    private void createAuditLog(Transaction transaction, String action, String oldValues, String newValues,
            UUID userId) {
        try {
            AuditLog auditLog = new AuditLog(transaction, action, oldValues, newValues, userId);
            auditLogRepository.save(auditLog);
            logger.debug("Audit log created for transaction: {}, action: {}", transaction.getId(), action);
        } catch (Exception e) {
            logger.error("Failed to create audit log for transaction: {}", transaction.getId(), e);
        }
    }

    /**
     * Convert transaction to JSON string
     */
    private String convertTransactionToJson(Transaction transaction) {
        try {
            return objectMapper.writeValueAsString(convertToDTO(transaction));
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert transaction to JSON", e);
            return "{}";
        }
    }

    /**
     * Convert Transaction entity to DTO
     */
    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setPaymentId(transaction.getPaymentId());
        dto.setSenderUserId(transaction.getSenderUserId());
        dto.setReceiverUserId(transaction.getReceiverUserId());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setStatus(transaction.getStatus());
        dto.setDescription(transaction.getDescription());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }

    /**
     * Convert AuditLog entity to DTO
     */
    private AuditLogDTO convertAuditLogToDTO(AuditLog auditLog) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(auditLog.getId());
        dto.setTransactionId(auditLog.getTransaction().getId());
        dto.setAction(auditLog.getAction());
        dto.setOldValues(auditLog.getOldValues());
        dto.setNewValues(auditLog.getNewValues());
        dto.setUserId(auditLog.getUserId());
        dto.setCreatedAt(auditLog.getCreatedAt());
        return dto;
    }

    /**
     * Financial Summary DTO
     */
    public static class FinancialSummaryDTO {
        private Double totalSent;
        private Double totalReceived;
        private Long totalTransactions;

        public FinancialSummaryDTO(Double totalSent, Double totalReceived, Long totalTransactions) {
            this.totalSent = totalSent;
            this.totalReceived = totalReceived;
            this.totalTransactions = totalTransactions;
        }

        // Getters
        public Double getTotalSent() {
            return totalSent;
        }

        public Double getTotalReceived() {
            return totalReceived;
        }

        public Long getTotalTransactions() {
            return totalTransactions;
        }
    }
}
