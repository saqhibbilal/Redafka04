package com.riyada.ledgerservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.UUID;

@FeignClient(name = "payment-service", url = "${services.payment-service-url}")
public interface PaymentServiceClient {

    @GetMapping("/api/payments/{paymentId}")
    PaymentDetailsDTO getPaymentDetails(@PathVariable UUID paymentId,
            @RequestHeader("Authorization") String token);

    class PaymentDetailsDTO {
        private UUID id;
        private UUID senderUserId;
        private UUID receiverUserId;
        private String toEmail;
        private java.math.BigDecimal amount;
        private String currency;
        private String status;
        private String description;
        private String referenceId;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;

        // Getters and Setters
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
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

        public String getToEmail() {
            return toEmail;
        }

        public void setToEmail(String toEmail) {
            this.toEmail = toEmail;
        }

        public java.math.BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(java.math.BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
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

        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }

        public java.time.LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(java.time.LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public java.time.LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
