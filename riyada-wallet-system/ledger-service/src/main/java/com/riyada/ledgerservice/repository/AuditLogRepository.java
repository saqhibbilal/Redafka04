package com.riyada.ledgerservice.repository;

import com.riyada.ledgerservice.entity.AuditLog;
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
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    // Find audit logs by transaction ID
    List<AuditLog> findByTransactionIdOrderByCreatedAtAsc(UUID transactionId);

    // Find audit logs by user ID
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Find audit logs by action
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);

    // Find audit logs within date range
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Find audit logs by transaction ID with pagination
    Page<AuditLog> findByTransactionIdOrderByCreatedAtAsc(UUID transactionId, Pageable pageable);

    // Find recent audit logs for a transaction
    @Query("SELECT a FROM AuditLog a WHERE a.transaction.id = :transactionId ORDER BY a.createdAt DESC")
    List<AuditLog> findRecentAuditLogsByTransactionId(@Param("transactionId") UUID transactionId, Pageable pageable);

    // Count audit logs by transaction
    long countByTransactionId(UUID transactionId);

    // Find audit logs by user and date range
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<AuditLog> findByUserIdAndDateRange(@Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
