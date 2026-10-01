package com.mouaad.escrowservice.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@RequiredArgsConstructor
public class JobDto {
    private UUID id;
    private UUID clientId;
}
