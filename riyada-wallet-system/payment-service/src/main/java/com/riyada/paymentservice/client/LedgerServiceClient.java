package com.riyada.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "ledger-service", url = "${services.ledger-service-url}")
public interface LedgerServiceClient {

    @PostMapping("/api/ledger/record-transaction")
    Map<String, Object> recordTransaction(@RequestBody RecordTransactionRequest request);

    class RecordTransactionRequest {
        private UUID paymentId;
        private UUID senderUserId;
        private UUID receiverUserId;
        private java.math.BigDecimal amount;
        private String currency;
        private String transactionType;
        private String status;
        private String description;

        // Constructors
        public RecordTransactionRequest() {
        }

        public RecordTransactionRequest(UUID paymentId, UUID senderUserId, UUID receiverUserId,
                java.math.BigDecimal amount, String currency, String transactionType,
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
}
