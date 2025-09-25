package com.riyada.walletservice.repository;

import com.riyada.walletservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    /**
     * Find wallet by user ID
     */
    Optional<Wallet> findByUserId(UUID userId);

    /**
     * Find active wallet by user ID
     */
    @Query("SELECT w FROM Wallet w WHERE w.userId = :userId AND w.isActive = true")
    Optional<Wallet> findActiveWalletByUserId(@Param("userId") UUID userId);

    /**
     * Find all wallets for a user (including inactive ones)
     */
    List<Wallet> findAllByUserId(UUID userId);

    /**
     * Check if a wallet exists for a user
     */
    boolean existsByUserId(UUID userId);

    /**
     * Check if an active wallet exists for a user
     */
    @Query("SELECT COUNT(w) > 0 FROM Wallet w WHERE w.userId = :userId AND w.isActive = true")
    boolean existsActiveWalletByUserId(@Param("userId") UUID userId);

    /**
     * Find all active wallets
     */
    List<Wallet> findByIsActiveTrue();

    /**
     * Find wallets by currency
     */
    List<Wallet> findByCurrency(String currency);

    /**
     * Find active wallets by currency
     */
    List<Wallet> findByCurrencyAndIsActiveTrue(String currency);
}
