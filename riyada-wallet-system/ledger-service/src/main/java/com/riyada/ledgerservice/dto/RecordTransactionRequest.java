package com.riyada.ledgerservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class RecordTransactionRequest {

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
    private String transactionType;

    @NotNull(message = "Status is required")
    private String status;

    private String description;

    // Constructors
    public RecordTransactionRequest() {
    }

    public RecordTransactionRequest(UUID paymentId, UUID senderUserId, UUID receiverUserId,
            BigDecimal amount, String currency, String transactionType,
            String status, String description) {
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
