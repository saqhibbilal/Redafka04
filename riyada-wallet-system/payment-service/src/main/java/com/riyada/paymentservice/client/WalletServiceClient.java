package com.riyada.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "wallet-service", url = "${services.wallet-service.url}")
public interface WalletServiceClient {

    @GetMapping("/api/wallets/user/{userId}/balance")
    Map<String, Object> getWalletBalance(@PathVariable("userId") String userId,
            @RequestHeader("Authorization") String authorization);

    @PostMapping("/api/wallets/user/{userId}/debit")
    Map<String, Object> debitWallet(@PathVariable("userId") String userId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "description", required = false) String description,
            @RequestHeader("Authorization") String authorization);

    @PostMapping("/api/wallets/user/{userId}/credit")
    Map<String, Object> creditWallet(@PathVariable("userId") String userId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "description", required = false) String description,
            @RequestHeader("Authorization") String authorization);
}
