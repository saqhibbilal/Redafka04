package com.riyada.walletservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {

    @GetMapping("${user-service.endpoints.profile}/{userId}")
    Map<String, Object> getUserProfile(@PathVariable("userId") String userId,
            @RequestHeader("Authorization") String authorization);

    @GetMapping("${user-service.endpoints.profile}/email/{email}")
    Map<String, Object> getUserProfileByEmail(@PathVariable("email") String email,
            @RequestHeader("Authorization") String authorization);
}
