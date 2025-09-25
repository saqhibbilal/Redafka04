package com.riyada.walletservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class TransferRequestDTO {

    @NotNull(message = "From user ID is required")
    private String fromUserId;

    @Email(message = "Recipient email should be valid")
    @NotNull(message = "Recipient email is required")
    private String toEmail;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    // Constructors
    public TransferRequestDTO() {
    }

    public TransferRequestDTO(String fromUserId, String toEmail, BigDecimal amount, String description) {
        this.fromUserId = fromUserId;
        this.toEmail = toEmail;
        this.amount = amount;
        this.description = description;
    }

    // Getters and Setters
    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TransferRequestDTO{" +
                "fromUserId='" + fromUserId + '\'' +
                ", toEmail='" + toEmail + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}
