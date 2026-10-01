package com.mouaad.biddingservice.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJobStatusRequest {
    private String status;
}
