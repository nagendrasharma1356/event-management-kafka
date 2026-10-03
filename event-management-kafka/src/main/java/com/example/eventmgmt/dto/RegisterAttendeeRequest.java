package com.example.eventmgmt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterAttendeeRequest(@NotBlank String attendeeName, @NotBlank @Email String attendeeEmail) {
}
