package com.riyada.walletservice.controller;

import com.riyada.walletservice.dto.TransferRequestDTO;
import com.riyada.walletservice.dto.WalletCreateDTO;
import com.riyada.walletservice.dto.WalletResponseDTO;
import com.riyada.walletservice.dto.WalletTransactionResponseDTO;
import com.riyada.walletservice.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = "*")
public class WalletController {

    @Autowired
    private WalletService walletService;

    /**
     * Health check endpoint
     * GET /api/wallets/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "wallet-service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new wallet
     * POST /api/wallets/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createWallet(@Valid @RequestBody WalletCreateDTO createDTO) {
        try {
            WalletResponseDTO wallet = walletService.createWallet(createDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Wallet created successfully");
            response.put("wallet", wallet);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "WALLET_CREATION_FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred during wallet creation");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Create wallet for user (auto-create on user registration)
     * POST /api/wallets/create-for-user/{userId}
     */
    @PostMapping("/create-for-user/{userId}")
    public ResponseEntity<?> createWalletForUser(@PathVariable UUID userId) {
        try {
            WalletResponseDTO wallet = walletService.createWalletForUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Wallet created successfully for user");
            response.put("wallet", wallet);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "WALLET_CREATION_FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred during wallet creation");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get wallet by user ID
     * GET /api/wallets/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getWalletByUserId(@PathVariable UUID userId) {
        try {
            WalletResponseDTO wallet = walletService.getWalletByUserId(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Wallet retrieved successfully");
            response.put("wallet", wallet);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "WALLET_NOT_FOUND");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while retrieving wallet");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get wallet balance by user ID
     * GET /api/wallets/user/{userId}/balance
     */
    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<?> getWalletBalance(@PathVariable UUID userId) {
        try {
            BigDecimal balance = walletService.getWalletBalance(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Balance retrieved successfully");
            response.put("balance", balance);
            response.put("currency", "USD");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "WALLET_NOT_FOUND");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while retrieving balance");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Credit amount to wallet
     * POST /api/wallets/user/{userId}/credit
     */
    @PostMapping("/user/{userId}/credit")
    public ResponseEntity<?> creditWallet(@PathVariable UUID userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {
        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Amount must be greater than zero");
                errorResponse.put("error", "INVALID_AMOUNT");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            WalletResponseDTO wallet = walletService.creditWallet(userId, amount,
                    description != null ? description : "Wallet credit");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Amount credited successfully");
            response.put("wallet", wallet);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "CREDIT_FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred during credit");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Debit amount from wallet
     * POST /api/wallets/user/{userId}/debit
     */
    @PostMapping("/user/{userId}/debit")
    public ResponseEntity<?> debitWallet(@PathVariable UUID userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {
        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Amount must be greater than zero");
                errorResponse.put("error", "INVALID_AMOUNT");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            WalletResponseDTO wallet = walletService.debitWallet(userId, amount,
                    description != null ? description : "Wallet debit");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Amount debited successfully");
            response.put("wallet", wallet);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "DEBIT_FAILED");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred during debit");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Transfer amount between wallets
     * POST /api/wallets/transfer
     * 
     * NOTE: This endpoint is deprecated. Please use the Payment Service for
     * transfers.
     * POST /api/payments/transfer
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transferAmount(@Valid @RequestBody TransferRequestDTO transferRequest) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message",
                "Transfer functionality has been moved to Payment Service. Please use POST /api/payments/transfer");
        response.put("error", "DEPRECATED");
        response.put("redirect", "/api/payments/transfer");

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body(response);
    }

    /**
     * Get wallet transactions
     * GET /api/wallets/user/{userId}/transactions
     */
    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<?> getWalletTransactions(@PathVariable UUID userId) {
        try {
            List<WalletTransactionResponseDTO> transactions = walletService.getWalletTransactions(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transactions retrieved successfully");
            response.put("transactions", transactions);
            response.put("count", transactions.size());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "WALLET_NOT_FOUND");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while retrieving transactions");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Check if wallet exists for user
     * GET /api/wallets/user/{userId}/exists
     */
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<?> walletExists(@PathVariable UUID userId) {
        try {
            boolean exists = walletService.walletExists(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            response.put("userId", userId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred while checking wallet existence");
            errorResponse.put("error", "INTERNAL_SERVER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
