package com.riyada.paymentservice.dto;

import com.riyada.paymentservice.entity.Payment;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentStatusDTO {

    private UUID id;
    private String referenceId;
    private Payment.PaymentStatus status;
    private String failureReason;
    private LocalDateTime processedAt;

    // Constructors
    public PaymentStatusDTO() {
    }

    public PaymentStatusDTO(UUID id, String referenceId, Payment.PaymentStatus status,
            String failureReason, LocalDateTime processedAt) {
        this.id = id;
        this.referenceId = referenceId;
        this.status = status;
        this.failureReason = failureReason;
        this.processedAt = processedAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Payment.PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(Payment.PaymentStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "PaymentStatusDTO{" +
                "id=" + id +
                ", referenceId='" + referenceId + '\'' +
                ", status=" + status +
                ", failureReason='" + failureReason + '\'' +
                ", processedAt=" + processedAt +
                '}';
    }
}
