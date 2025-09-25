package com.riyada.walletservice.repository;

import com.riyada.walletservice.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {

    /**
     * Find all transactions for a specific wallet
     */
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(UUID walletId);

    /**
     * Find all transactions for a specific wallet with pagination
     */
    Page<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(UUID walletId, Pageable pageable);

    /**
     * Find transactions by wallet ID and transaction type
     */
    List<WalletTransaction> findByWalletIdAndTransactionTypeOrderByCreatedAtDesc(
            UUID walletId, WalletTransaction.TransactionType transactionType);

    /**
     * Find transactions for a specific wallet within a date range
     */
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.walletId = :walletId " +
            "AND wt.createdAt BETWEEN :startDate AND :endDate ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findByWalletIdAndDateRange(
            @Param("walletId") UUID walletId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find transactions for a specific wallet within a date range with pagination
     */
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.walletId = :walletId " +
            "AND wt.createdAt BETWEEN :startDate AND :endDate ORDER BY wt.createdAt DESC")
    Page<WalletTransaction> findByWalletIdAndDateRange(
            @Param("walletId") UUID walletId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Find transactions by wallet ID and amount range
     */
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.walletId = :walletId " +
            "AND wt.amount BETWEEN :minAmount AND :maxAmount ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findByWalletIdAndAmountRange(
            @Param("walletId") UUID walletId,
            @Param("minAmount") java.math.BigDecimal minAmount,
            @Param("maxAmount") java.math.BigDecimal maxAmount);

    /**
     * Count transactions for a specific wallet
     */
    long countByWalletId(UUID walletId);

    /**
     * Count transactions for a specific wallet by transaction type
     */
    long countByWalletIdAndTransactionType(UUID walletId, WalletTransaction.TransactionType transactionType);

    /**
     * Find latest transaction for a specific wallet
     */
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.walletId = :walletId ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findLatestByWalletId(@Param("walletId") UUID walletId, Pageable pageable);
}
