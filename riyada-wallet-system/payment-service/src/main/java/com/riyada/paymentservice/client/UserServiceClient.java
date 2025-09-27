package com.riyada.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "user-service", url = "${services.user-service.url}")
public interface UserServiceClient {

        @GetMapping("/api/users/profile/{userId}")
        Map<String, Object> getUserProfile(@PathVariable("userId") String userId,
                        @RequestHeader("Authorization") String authorization);

        @GetMapping("/api/users/profile/email/{email}")
        Map<String, Object> getUserProfileByEmail(@PathVariable("email") String email,
                        @RequestHeader(value = "Authorization", required = false) String authorization);
}
