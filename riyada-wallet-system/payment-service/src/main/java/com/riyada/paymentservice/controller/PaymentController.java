package com.riyada.paymentservice.controller;

import com.riyada.paymentservice.config.JwtConfig;
import com.riyada.paymentservice.dto.PaymentRequestDTO;
import com.riyada.paymentservice.dto.PaymentResponseDTO;
import com.riyada.paymentservice.dto.PaymentStatusDTO;
import com.riyada.paymentservice.entity.Payment;
import com.riyada.paymentservice.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Health check endpoint
     * GET /api/payments/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "payment-service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Process a payment transfer
     * POST /api/payments/transfer
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequestDTO requestDTO,
            HttpServletRequest request) {
        try {
            // Extract user ID from JWT token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authorization header is required");
                errorResponse.put("error", "UNAUTHORIZED");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String token = authHeader.substring(7);
            UUID fromUserId = jwtConfig.getUserIdFromToken(token);

            // Process the payment
            PaymentResponseDTO payment = paymentService.processPayment(requestDTO, fromUserId, token);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment processed successfully");
            response.put("payment", payment);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "PAYMENT_FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred during payment processing");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payment by ID
     * GET /api/payments/{paymentId}
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable UUID paymentId,
            HttpServletRequest request) {
        try {
            // Extract user ID from JWT token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authorization header is required");
                errorResponse.put("error", "UNAUTHORIZED");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String token = authHeader.substring(7);
            UUID userId = jwtConfig.getUserIdFromToken(token);

            PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);

            // Check if user is authorized to view this payment
            if (!payment.getFromUserId().equals(userId) && !payment.getToUserId().equals(userId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "You are not authorized to view this payment");
                errorResponse.put("error", "FORBIDDEN");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment retrieved successfully");
            response.put("payment", payment);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "PAYMENT_NOT_FOUND");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while retrieving payment");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payment status by reference ID
     * GET /api/payments/status/{referenceId}
     */
    @GetMapping("/status/{referenceId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String referenceId) {
        try {
            PaymentStatusDTO status = paymentService.getPaymentStatusByReference(referenceId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment status retrieved successfully");
            response.put("status", status);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "PAYMENT_NOT_FOUND");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while retrieving payment status");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get user's payment history
     * GET /api/payments/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPayments(@PathVariable UUID userId,
            HttpServletRequest request) {
        try {
            // Extract user ID from JWT token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authorization header is required");
                errorResponse.put("error", "UNAUTHORIZED");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String token = authHeader.substring(7);
            UUID tokenUserId = jwtConfig.getUserIdFromToken(token);

            // Check if user is authorized to view this user's payments
            if (!tokenUserId.equals(userId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "You are not authorized to view this user's payments");
                errorResponse.put("error", "FORBIDDEN");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            List<PaymentResponseDTO> payments = paymentService.getUserPayments(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User payments retrieved successfully");
            response.put("payments", payments);
            response.put("count", payments.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while retrieving user payments");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Cancel a pending payment
     * POST /api/payments/{paymentId}/cancel
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable UUID paymentId,
            HttpServletRequest request) {
        try {
            // Extract user ID from JWT token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authorization header is required");
                errorResponse.put("error", "UNAUTHORIZED");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String token = authHeader.substring(7);
            UUID userId = jwtConfig.getUserIdFromToken(token);

            PaymentResponseDTO payment = paymentService.cancelPayment(paymentId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment cancelled successfully");
            response.put("payment", payment);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "CANCELLATION_FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred during payment cancellation");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
