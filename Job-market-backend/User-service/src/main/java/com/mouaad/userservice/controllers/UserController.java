package com.mouaad.userservice.controllers;

import com.mouaad.userservice.dtos.CreateReviewRequest;
import com.mouaad.userservice.dtos.ReviewDto;
import com.mouaad.userservice.dtos.UpdateProfileRequest;
import com.mouaad.userservice.dtos.UserDto;
import com.mouaad.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateProfile(@PathVariable UUID userId, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @GetMapping("/{userId}/reviews")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getReviewsForUser(userId));
    }

    @PostMapping("/{userId}/reviews")
    public ResponseEntity<ReviewDto> addReview(@PathVariable UUID userId,@Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addReview(userId, request));
    }

}