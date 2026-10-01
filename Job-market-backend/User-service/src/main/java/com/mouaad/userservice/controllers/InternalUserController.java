package com.mouaad.userservice.controllers;

import com.mouaad.userservice.dtos.CreateUserProfileRequest;
import com.mouaad.userservice.dtos.UserDto;
import com.mouaad.userservice.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/user")
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> createProfile(@Valid @RequestBody CreateUserProfileRequest request) {
        UserDto created = userService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}