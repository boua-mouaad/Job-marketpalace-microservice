package com.mouaad.biddingservice.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    public UUID id;
    public String role; // "FREELANCER", "CLIENT", etc.
}
