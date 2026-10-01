package com.mouaad.authservice.clients;

import com.mouaad.authservice.dtos.CreateUserProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient{
    @PostMapping("/api/internal/users/profile")
    void createProfile(@RequestBody CreateUserProfileRequest request);
}
