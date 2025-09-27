package com.riyada.ledgerservice.repository;

import com.riyada.ledgerservice.entity.Transaction;
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
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Find transactions by user ID (both sender and receiver)
    @Query("SELECT t FROM Transaction t WHERE t.senderUserId = :userId OR t.receiverUserId = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    // Find transactions by sender user ID
    @Query("SELECT t FROM Transaction t WHERE t.senderUserId = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findBySenderUserId(@Param("userId") UUID userId, Pageable pageable);

    // Find transactions by receiver user ID
    @Query("SELECT t FROM Transaction t WHERE t.receiverUserId = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findByReceiverUserId(@Param("userId") UUID userId, Pageable pageable);

    // Find transactions by payment ID
    List<Transaction> findByPaymentId(UUID paymentId);

    // Find transactions by status
    List<Transaction> findByStatus(Transaction.TransactionStatus status);

    // Find transactions by transaction type
    List<Transaction> findByTransactionType(Transaction.TransactionType transactionType);

    // Find transactions within date range
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Find transactions by user and date range
    @Query("SELECT t FROM Transaction t WHERE (t.senderUserId = :userId OR t.receiverUserId = :userId) AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserIdAndDateRange(@Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Count transactions by user
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.senderUserId = :userId OR t.receiverUserId = :userId")
    long countByUserId(@Param("userId") UUID userId);

    // Sum total amount sent by user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.senderUserId = :userId AND t.status = 'COMPLETED'")
    Double getTotalSentByUser(@Param("userId") UUID userId);

    // Sum total amount received by user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.receiverUserId = :userId AND t.status = 'COMPLETED'")
    Double getTotalReceivedByUser(@Param("userId") UUID userId);

    // Find recent transactions for a user
    @Query("SELECT t FROM Transaction t WHERE t.senderUserId = :userId OR t.receiverUserId = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactionsByUserId(@Param("userId") UUID userId, Pageable pageable);
}
