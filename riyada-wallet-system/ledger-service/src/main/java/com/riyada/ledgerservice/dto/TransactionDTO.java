package com.riyada.ledgerservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.riyada.ledgerservice.entity.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDTO {

    private UUID id;

    @NotNull(message = "Payment ID is required")
    private UUID paymentId;

    @NotNull(message = "Sender user ID is required")
    private UUID senderUserId;

    @NotNull(message = "Receiver user ID is required")
    private UUID receiverUserId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotNull(message = "Transaction type is required")
    private Transaction.TransactionType transactionType;

    @NotNull(message = "Status is required")
    private Transaction.TransactionStatus status;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Constructors
    public TransactionDTO() {
    }

    public TransactionDTO(UUID paymentId, UUID senderUserId, UUID receiverUserId,
            BigDecimal amount, String currency, Transaction.TransactionType transactionType,
            Transaction.TransactionStatus status, String description) {
        this.paymentId = paymentId;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.status = status;
        this.description = description;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(UUID senderUserId) {
        this.senderUserId = senderUserId;
    }

    public UUID getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(UUID receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Transaction.TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Transaction.TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Transaction.TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(Transaction.TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
