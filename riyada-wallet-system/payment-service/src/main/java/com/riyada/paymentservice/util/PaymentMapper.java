package com.riyada.paymentservice.util;

import com.riyada.paymentservice.dto.PaymentRequestDTO;
import com.riyada.paymentservice.dto.PaymentResponseDTO;
import com.riyada.paymentservice.dto.PaymentStatusDTO;
import com.riyada.paymentservice.entity.Payment;

import java.util.UUID;

public class PaymentMapper {

    /**
     * Convert Payment entity to PaymentResponseDTO
     */
    public static PaymentResponseDTO toPaymentResponseDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        return new PaymentResponseDTO(
                payment.getId(),
                payment.getFromUserId(),
                payment.getToUserId(),
                payment.getToEmail(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getDescription(),
                payment.getReferenceId(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getProcessedAt());
    }

    /**
     * Convert Payment entity to PaymentStatusDTO
     */
    public static PaymentStatusDTO toPaymentStatusDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        return new PaymentStatusDTO(
                payment.getId(),
                payment.getReferenceId(),
                payment.getStatus(),
                payment.getFailureReason(),
                payment.getProcessedAt());
    }

    /**
     * Create Payment entity from PaymentRequestDTO and user IDs
     */
    public static Payment toPayment(PaymentRequestDTO requestDTO, UUID fromUserId, UUID toUserId) {
        if (requestDTO == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setFromUserId(fromUserId);
        payment.setToUserId(toUserId);
        payment.setToEmail(requestDTO.getToEmail());
        payment.setAmount(requestDTO.getAmount());
        payment.setDescription(requestDTO.getDescription());
        payment.setCurrency("USD"); // Default currency
        payment.setStatus(Payment.PaymentStatus.PENDING);

        return payment;
    }
}
