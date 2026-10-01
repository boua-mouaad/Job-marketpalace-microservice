package com.mouaad.jobservice.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UserProfileDto {
    private UUID id;
    private String fullName;
    private Double ratingAvg;
}
