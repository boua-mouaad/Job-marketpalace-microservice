package com.mouaad.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private UUID id;
    private UUID jobId;
    private UUID reviewerId;
    private UUID revieweeId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
