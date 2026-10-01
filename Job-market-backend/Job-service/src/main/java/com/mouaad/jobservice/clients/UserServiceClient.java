package com.mouaad.jobservice.clients;

import com.mouaad.jobservice.dtos.UserProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    UserProfileDto getUserProfile(@PathVariable("id") UUID id );
}

