package com.riyada.ledgerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients
public class LedgerServiceApplication {

    public static void main(String[] args) {
        // Set timezone to UTC before Spring Boot starts
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");

        SpringApplication.run(LedgerServiceApplication.class, args);
    }
}
