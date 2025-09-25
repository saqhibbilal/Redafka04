package com.riyada.walletservice.util;

import com.riyada.walletservice.dto.WalletCreateDTO;
import com.riyada.walletservice.dto.WalletResponseDTO;
import com.riyada.walletservice.dto.WalletTransactionResponseDTO;
import com.riyada.walletservice.entity.Wallet;
import com.riyada.walletservice.entity.WalletTransaction;

public class WalletMapper {

    /**
     * Convert WalletCreateDTO to Wallet entity
     */
    public static Wallet toWallet(WalletCreateDTO createDTO) {
        Wallet wallet = new Wallet();
        wallet.setUserId(createDTO.getUserId());
        wallet.setCurrency(createDTO.getCurrency());
        return wallet;
    }

    /**
     * Convert Wallet entity to WalletResponseDTO
     */
    public static WalletResponseDTO toWalletResponseDTO(Wallet wallet) {
        WalletResponseDTO responseDTO = new WalletResponseDTO();
        responseDTO.setId(wallet.getId());
        responseDTO.setUserId(wallet.getUserId());
        responseDTO.setBalance(wallet.getBalance());
        responseDTO.setCurrency(wallet.getCurrency());
        responseDTO.setIsActive(wallet.getIsActive());
        responseDTO.setCreatedAt(wallet.getCreatedAt());
        responseDTO.setUpdatedAt(wallet.getUpdatedAt());
        return responseDTO;
    }

    /**
     * Convert WalletTransaction entity to WalletTransactionResponseDTO
     */
    public static WalletTransactionResponseDTO toWalletTransactionResponseDTO(WalletTransaction transaction) {
        WalletTransactionResponseDTO responseDTO = new WalletTransactionResponseDTO();
        responseDTO.setId(transaction.getId());
        responseDTO.setWalletId(transaction.getWalletId());
        responseDTO.setTransactionType(transaction.getTransactionType());
        responseDTO.setAmount(transaction.getAmount());
        responseDTO.setBalanceBefore(transaction.getBalanceBefore());
        responseDTO.setBalanceAfter(transaction.getBalanceAfter());
        responseDTO.setDescription(transaction.getDescription());
        responseDTO.setCreatedAt(transaction.getCreatedAt());
        return responseDTO;
    }
}
