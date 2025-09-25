package com.riyada.walletservice.service;

import com.riyada.walletservice.client.UserServiceClient;
import com.riyada.walletservice.dto.WalletCreateDTO;
import com.riyada.walletservice.dto.WalletResponseDTO;
import com.riyada.walletservice.dto.WalletTransactionResponseDTO;
import com.riyada.walletservice.entity.Wallet;
import com.riyada.walletservice.entity.WalletTransaction;
import com.riyada.walletservice.repository.WalletRepository;
import com.riyada.walletservice.repository.WalletTransactionRepository;
import com.riyada.walletservice.util.WalletMapper;
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
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    /**
     * Create a new wallet for a user
     */
    public WalletResponseDTO createWallet(WalletCreateDTO createDTO) {
        // Check if user already has an active wallet
        if (walletRepository.existsActiveWalletByUserId(createDTO.getUserId())) {
            throw new RuntimeException("User already has an active wallet");
        }

        // Create new wallet
        Wallet wallet = WalletMapper.toWallet(createDTO);
        wallet = walletRepository.save(wallet);

        // Create initial transaction record
        createTransactionRecord(wallet.getId(), WalletTransaction.TransactionType.CREDIT,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                "Wallet created");

        return WalletMapper.toWalletResponseDTO(wallet);
    }

    /**
     * Create wallet for user (auto-create on user registration)
     */
    public WalletResponseDTO createWalletForUser(UUID userId) {
        return createWalletForUser(userId, "USD");
    }

    /**
     * Create wallet for user with specific currency
     */
    public WalletResponseDTO createWalletForUser(UUID userId, String currency) {
        WalletCreateDTO createDTO = new WalletCreateDTO(userId, currency);
        return createWallet(createDTO);
    }

    /**
     * Get wallet by user ID
     */
    @Transactional(readOnly = true)
    public WalletResponseDTO getWalletByUserId(UUID userId) {
        Optional<Wallet> wallet = walletRepository.findActiveWalletByUserId(userId);
        if (wallet.isEmpty()) {
            throw new RuntimeException("Wallet not found for user: " + userId);
        }
        return WalletMapper.toWalletResponseDTO(wallet.get());
    }

    /**
     * Get wallet balance by user ID
     */
    @Transactional(readOnly = true)
    public BigDecimal getWalletBalance(UUID userId) {
        Optional<Wallet> wallet = walletRepository.findActiveWalletByUserId(userId);
        if (wallet.isEmpty()) {
            throw new RuntimeException("Wallet not found for user: " + userId);
        }
        return wallet.get().getBalance();
    }

    /**
     * Credit amount to wallet
     */
    public WalletResponseDTO creditWallet(UUID userId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Credit amount must be greater than zero");
        }

        Optional<Wallet> walletOpt = walletRepository.findActiveWalletByUserId(userId);
        if (walletOpt.isEmpty()) {
            throw new RuntimeException("Wallet not found for user: " + userId);
        }

        Wallet wallet = walletOpt.get();
        BigDecimal balanceBefore = wallet.getBalance();
        wallet.credit(amount);
        wallet = walletRepository.save(wallet);

        // Create transaction record
        createTransactionRecord(wallet.getId(), WalletTransaction.TransactionType.CREDIT,
                amount, balanceBefore, wallet.getBalance(), description);

        return WalletMapper.toWalletResponseDTO(wallet);
    }

    /**
     * Debit amount from wallet
     */
    public WalletResponseDTO debitWallet(UUID userId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Debit amount must be greater than zero");
        }

        Optional<Wallet> walletOpt = walletRepository.findActiveWalletByUserId(userId);
        if (walletOpt.isEmpty()) {
            throw new RuntimeException("Wallet not found for user: " + userId);
        }

        Wallet wallet = walletOpt.get();

        // Check sufficient balance
        if (!wallet.hasSufficientBalance(amount)) {
            throw new RuntimeException("Insufficient balance. Available: " + wallet.getBalance());
        }

        BigDecimal balanceBefore = wallet.getBalance();
        wallet.debit(amount);
        wallet = walletRepository.save(wallet);

        // Create transaction record
        createTransactionRecord(wallet.getId(), WalletTransaction.TransactionType.DEBIT,
                amount, balanceBefore, wallet.getBalance(), description);

        return WalletMapper.toWalletResponseDTO(wallet);
    }

    /**
     * Transfer amount between wallets
     */
    public Map<String, Object> transferAmount(UUID fromUserId, UUID toUserId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }

        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("Cannot transfer to the same wallet");
        }

        // Get both wallets
        Optional<Wallet> fromWalletOpt = walletRepository.findActiveWalletByUserId(fromUserId);
        Optional<Wallet> toWalletOpt = walletRepository.findActiveWalletByUserId(toUserId);

        if (fromWalletOpt.isEmpty()) {
            throw new RuntimeException("Sender wallet not found");
        }
        if (toWalletOpt.isEmpty()) {
            throw new RuntimeException("Recipient wallet not found");
        }

        Wallet fromWallet = fromWalletOpt.get();
        Wallet toWallet = toWalletOpt.get();

        // Check sufficient balance
        if (!fromWallet.hasSufficientBalance(amount)) {
            throw new RuntimeException("Insufficient balance. Available: " + fromWallet.getBalance());
        }

        // Perform transfer atomically
        BigDecimal fromBalanceBefore = fromWallet.getBalance();
        BigDecimal toBalanceBefore = toWallet.getBalance();

        fromWallet.debit(amount);
        toWallet.credit(amount);

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        // Create transaction records
        createTransactionRecord(fromWallet.getId(), WalletTransaction.TransactionType.DEBIT,
                amount, fromBalanceBefore, fromWallet.getBalance(),
                description != null ? description : "Transfer to user: " + toUserId);

        createTransactionRecord(toWallet.getId(), WalletTransaction.TransactionType.CREDIT,
                amount, toBalanceBefore, toWallet.getBalance(),
                description != null ? description : "Transfer from user: " + fromUserId);

        return Map.of(
                "success", true,
                "fromWallet", WalletMapper.toWalletResponseDTO(fromWallet),
                "toWallet", WalletMapper.toWalletResponseDTO(toWallet),
                "amount", amount,
                "description", description != null ? description : "Transfer completed");
    }

    /**
     * Get wallet transactions
     */
    @Transactional(readOnly = true)
    public List<WalletTransactionResponseDTO> getWalletTransactions(UUID userId) {
        Optional<Wallet> wallet = walletRepository.findActiveWalletByUserId(userId);
        if (wallet.isEmpty()) {
            throw new RuntimeException("Wallet not found for user: " + userId);
        }

        List<WalletTransaction> transactions = walletTransactionRepository
                .findByWalletIdOrderByCreatedAtDesc(wallet.get().getId());

        return transactions.stream()
                .map(WalletMapper::toWalletTransactionResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if wallet exists for user
     */
    @Transactional(readOnly = true)
    public boolean walletExists(UUID userId) {
        return walletRepository.existsActiveWalletByUserId(userId);
    }

    /**
     * Deactivate wallet
     */
    public WalletResponseDTO deactivateWallet(UUID userId) {
        Optional<Wallet> walletOpt = walletRepository.findActiveWalletByUserId(userId);
        if (walletOpt.isEmpty()) {
            throw new RuntimeException("Wallet not found for user: " + userId);
        }

        Wallet wallet = walletOpt.get();
        wallet.setIsActive(false);
        wallet = walletRepository.save(wallet);

        return WalletMapper.toWalletResponseDTO(wallet);
    }

    /**
     * Create transaction record
     */
    private void createTransactionRecord(UUID walletId, WalletTransaction.TransactionType type,
            BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter,
            String description) {
        WalletTransaction transaction = new WalletTransaction(
                walletId, type, amount, balanceBefore, balanceAfter, description);
        walletTransactionRepository.save(transaction);
    }
}
