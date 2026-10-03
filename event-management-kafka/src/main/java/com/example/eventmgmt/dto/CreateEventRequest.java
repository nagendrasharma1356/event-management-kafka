package com.example.eventmgmt.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateEventRequest(
        @NotBlank String name,
        String venue,
        @NotNull @Future LocalDateTime eventDate,
        @Min(1) int capacity) {
}
