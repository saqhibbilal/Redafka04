package com.riyada.walletservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class WalletCreateDTO {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @Size(max = 3, message = "Currency must not exceed 3 characters")
    private String currency = "USD";

    // Constructors
    public WalletCreateDTO() {
    }

    public WalletCreateDTO(UUID userId) {
        this.userId = userId;
        this.currency = "USD";
    }

    public WalletCreateDTO(UUID userId, String currency) {
        this.userId = userId;
        this.currency = currency;
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "WalletCreateDTO{" +
                "userId=" + userId +
                ", currency='" + currency + '\'' +
                '}';
    }
}
