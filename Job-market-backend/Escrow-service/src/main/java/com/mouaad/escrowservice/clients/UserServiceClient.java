package com.mouaad.escrowservice.clients;

import com.mouaad.escrowservice.dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "User-service")
public interface UserServiceClient {

    @GetMapping("/user/{userId}")
    UserDto getUserById(@PathVariable("userId") UUID userId);
}