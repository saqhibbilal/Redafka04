package com.riyada.ledgerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimezoneConfig {

    @Bean
    public String setTimezone() {
        // Set timezone to UTC for consistency across all services
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        return "UTC";
    }
}
