package com.mouaad.userservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserProfileRequest {

    private UUID id;

    @NotBlank(message = "fullName is required")
    @Size(max = 255, message = "fullName must be at most 255 characters")
    private String fullName;

    @Size(max = 1000, message = "bio must be at most 1000 characters")
    private String bio;

    private Set<String> skills;

    @Size(max = 2048, message = "profileImageUrl must be at most 2048 characters")
    private String profileImageUrl;
}
