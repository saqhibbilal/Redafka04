package com.riyada.paymentservice.service;

import com.riyada.paymentservice.client.UserServiceClient;
import com.riyada.paymentservice.client.WalletServiceClient;
import com.riyada.paymentservice.dto.PaymentRequestDTO;
import com.riyada.paymentservice.dto.PaymentResponseDTO;
import com.riyada.paymentservice.dto.PaymentStatusDTO;
import com.riyada.paymentservice.entity.Payment;
import com.riyada.paymentservice.repository.PaymentRepository;
import com.riyada.paymentservice.util.PaymentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private WalletServiceClient walletServiceClient;

    /**
     * Process a payment transfer between users
     */
    public PaymentResponseDTO processPayment(PaymentRequestDTO requestDTO, UUID fromUserId, String authToken) {
        logger.info("Processing payment from user {} to email {}", fromUserId, requestDTO.getToEmail());

        try {
            // Step 1: Validate recipient exists
            Map<String, Object> recipientResponse = userServiceClient.getUserProfileByEmail(
                    requestDTO.getToEmail(), null);

            if (!(Boolean) recipientResponse.get("success")) {
                throw new RuntimeException("Recipient not found: " + requestDTO.getToEmail());
            }

            Map<String, Object> recipientData = (Map<String, Object>) recipientResponse.get("user");
            UUID toUserId = UUID.fromString((String) recipientData.get("id"));

            // Step 2: Check if trying to transfer to self
            if (fromUserId.equals(toUserId)) {
                throw new RuntimeException("Cannot transfer money to yourself");
            }

            // Step 3: Check sender's balance
            Map<String, Object> balanceResponse = walletServiceClient.getWalletBalance(
                    fromUserId.toString(), "Bearer " + authToken);

            if (!(Boolean) balanceResponse.get("success")) {
                throw new RuntimeException("Failed to retrieve sender's balance");
            }

            BigDecimal currentBalance = new BigDecimal(balanceResponse.get("balance").toString());
            if (currentBalance.compareTo(requestDTO.getAmount()) < 0) {
                throw new RuntimeException("Insufficient balance. Available: " + currentBalance);
            }

            // Step 4: Create payment record
            Payment payment = PaymentMapper.toPayment(requestDTO, fromUserId, toUserId);
            payment.setStatus(Payment.PaymentStatus.PROCESSING);
            payment = paymentRepository.save(payment);

            logger.info("Payment created with ID: {} and reference: {}", payment.getId(), payment.getReferenceId());

            // Step 5: Process the transfer via wallet service
            try {
                // Debit from sender
                Map<String, Object> debitResponse = walletServiceClient.debitWallet(
                        fromUserId.toString(),
                        requestDTO.getAmount(),
                        "Transfer to " + requestDTO.getToEmail() + " - " + payment.getReferenceId(),
                        "Bearer " + authToken);

                if (!(Boolean) debitResponse.get("success")) {
                    throw new RuntimeException("Failed to debit from sender's wallet");
                }

                // Credit to recipient
                Map<String, Object> creditResponse = walletServiceClient.creditWallet(
                        toUserId.toString(),
                        requestDTO.getAmount(),
                        "Transfer from " + fromUserId + " - " + payment.getReferenceId(),
                        "Bearer " + authToken);

                if (!(Boolean) creditResponse.get("success")) {
                    // If credit fails, we need to reverse the debit
                    logger.error("Credit failed, attempting to reverse debit for payment: {}",
                            payment.getReferenceId());
                    walletServiceClient.creditWallet(
                            fromUserId.toString(),
                            requestDTO.getAmount(),
                            "Reversal for failed transfer - " + payment.getReferenceId(),
                            "Bearer " + authToken);

                    throw new RuntimeException("Failed to credit recipient's wallet");
                }

                // Step 6: Update payment status to completed
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setProcessedAt(LocalDateTime.now());
                payment = paymentRepository.save(payment);

                logger.info("Payment completed successfully: {}", payment.getReferenceId());

            } catch (Exception e) {
                // Update payment status to failed
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason(e.getMessage());
                payment = paymentRepository.save(payment);

                logger.error("Payment failed: {} - {}", payment.getReferenceId(), e.getMessage());
                throw e;
            }

            return PaymentMapper.toPaymentResponseDTO(payment);

        } catch (Exception e) {
            logger.error("Payment processing failed: {}", e.getMessage());
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * Get payment by ID
     */
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(UUID paymentId) {
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isEmpty()) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }
        return PaymentMapper.toPaymentResponseDTO(payment.get());
    }

    /**
     * Get payment status by reference ID
     */
    @Transactional(readOnly = true)
    public PaymentStatusDTO getPaymentStatusByReference(String referenceId) {
        Optional<Payment> payment = paymentRepository.findByReferenceId(referenceId);
        if (payment.isEmpty()) {
            throw new RuntimeException("Payment not found with reference: " + referenceId);
        }
        return PaymentMapper.toPaymentStatusDTO(payment.get());
    }

    /**
     * Get user's payment history
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getUserPayments(UUID userId) {
        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return payments.stream()
                .map(PaymentMapper::toPaymentResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user's sent payments
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getUserSentPayments(UUID userId) {
        List<Payment> payments = paymentRepository.findByFromUserIdOrderByCreatedAtDesc(userId);
        return payments.stream()
                .map(PaymentMapper::toPaymentResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user's received payments
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getUserReceivedPayments(UUID userId) {
        List<Payment> payments = paymentRepository.findByToUserIdOrderByCreatedAtDesc(userId);
        return payments.stream()
                .map(PaymentMapper::toPaymentResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payments by status
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByStatus(Payment.PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatusOrderByCreatedAtDesc(status);
        return payments.stream()
                .map(PaymentMapper::toPaymentResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cancel a pending payment
     */
    public PaymentResponseDTO cancelPayment(UUID paymentId, UUID userId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }

        Payment payment = paymentOpt.get();

        // Only allow cancellation of pending payments by the sender
        if (!payment.getFromUserId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own payments");
        }

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new RuntimeException("Only pending payments can be cancelled");
        }

        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        payment.setProcessedAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        logger.info("Payment cancelled: {}", payment.getReferenceId());
        return PaymentMapper.toPaymentResponseDTO(payment);
    }
}
