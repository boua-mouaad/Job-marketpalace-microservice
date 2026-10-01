package com.mouaad.authservice.dtos;

import lombok.AllArgsConstructor;

import java.util.UUID;
@AllArgsConstructor

public class CreateUserProfileRequest {
    public UUID id;
    public String fullName;
}


