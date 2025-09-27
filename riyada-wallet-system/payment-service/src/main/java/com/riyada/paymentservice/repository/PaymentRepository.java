package com.riyada.paymentservice.repository;

import com.riyada.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    /**
     * Find payments by from user ID
     */
    List<Payment> findByFromUserIdOrderByCreatedAtDesc(UUID fromUserId);

    /**
     * Find payments by to user ID
     */
    List<Payment> findByToUserIdOrderByCreatedAtDesc(UUID toUserId);

    /**
     * Find payments by user ID (either from or to)
     */
    @Query("SELECT p FROM Payment p WHERE p.fromUserId = :userId OR p.toUserId = :userId ORDER BY p.createdAt DESC")
    List<Payment> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    /**
     * Find payment by reference ID
     */
    Optional<Payment> findByReferenceId(String referenceId);

    /**
     * Find payments by status
     */
    List<Payment> findByStatusOrderByCreatedAtDesc(Payment.PaymentStatus status);

    /**
     * Find payments by from user ID and status
     */
    List<Payment> findByFromUserIdAndStatusOrderByCreatedAtDesc(UUID fromUserId, Payment.PaymentStatus status);

    /**
     * Find payments by to user ID and status
     */
    List<Payment> findByToUserIdAndStatusOrderByCreatedAtDesc(UUID toUserId, Payment.PaymentStatus status);

    /**
     * Count payments by from user ID
     */
    long countByFromUserId(UUID fromUserId);

    /**
     * Count payments by to user ID
     */
    long countByToUserId(UUID toUserId);

    /**
     * Count payments by status
     */
    long countByStatus(Payment.PaymentStatus status);

    /**
     * Check if payment exists by reference ID
     */
    boolean existsByReferenceId(String referenceId);
}
