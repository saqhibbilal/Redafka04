package com.riyada.paymentservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sender_user_id", nullable = false)
    @NotNull(message = "Sender user ID is required")
    private UUID fromUserId;

    @Column(name = "receiver_user_id", nullable = false)
    @NotNull(message = "Receiver user ID is required")
    private UUID toUserId;

    @Column(name = "to_email", nullable = false)
    @Email(message = "Recipient email should be valid")
    @NotNull(message = "Recipient email is required")
    private String toEmail;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    @NotNull(message = "Currency is required")
    private String currency = "USD";

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Payment status is required")
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Column(name = "reference_id", unique = true)
    private String referenceId;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    // Constructors
    public Payment() {
    }

    public Payment(UUID fromUserId, UUID toUserId, String toEmail, BigDecimal amount, String description) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.toEmail = toEmail;
        this.amount = amount;
        this.description = description;
        this.referenceId = generateReferenceId();
    }

    // Helper method to generate reference ID
    private String generateReferenceId() {
        return "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(UUID fromUserId) {
        this.fromUserId = fromUserId;
    }

    public UUID getToUserId() {
        return toUserId;
    }

    public void setToUserId(UUID toUserId) {
        this.toUserId = toUserId;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", toEmail='" + toEmail + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", failureReason='" + failureReason + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", processedAt=" + processedAt +
                '}';
    }

    // Payment Status Enum
    public enum PaymentStatus {
        PENDING, // Payment initiated but not processed
        PROCESSING, // Payment is being processed
        COMPLETED, // Payment completed successfully
        FAILED, // Payment failed
        CANCELLED // Payment was cancelled
    }
}
