package com.riyada.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class PaymentRequestDTO {

    @Email(message = "Recipient email should be valid")
    @NotNull(message = "Recipient email is required")
    private String toEmail;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    // Constructors
    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(String toEmail, BigDecimal amount, String description) {
        this.toEmail = toEmail;
        this.amount = amount;
        this.description = description;
    }

    // Getters and Setters
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
        return "PaymentRequestDTO{" +
                "toEmail='" + toEmail + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}
