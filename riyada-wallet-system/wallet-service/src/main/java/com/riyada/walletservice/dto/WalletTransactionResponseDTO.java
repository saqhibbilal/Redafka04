package com.riyada.walletservice.dto;

import com.riyada.walletservice.entity.WalletTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WalletTransactionResponseDTO {

    private UUID id;
    private UUID walletId;
    private WalletTransaction.TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime createdAt;

    // Constructors
    public WalletTransactionResponseDTO() {
    }

    public WalletTransactionResponseDTO(UUID id, UUID walletId, WalletTransaction.TransactionType transactionType,
            BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter,
            String description, LocalDateTime createdAt) {
        this.id = id;
        this.walletId = walletId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public WalletTransaction.TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(WalletTransaction.TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
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

    @Override
    public String toString() {
        return "WalletTransactionResponseDTO{" +
                "id=" + id +
                ", walletId=" + walletId +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", balanceBefore=" + balanceBefore +
                ", balanceAfter=" + balanceAfter +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
