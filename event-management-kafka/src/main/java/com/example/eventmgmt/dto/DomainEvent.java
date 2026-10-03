package com.example.eventmgmt.dto;

import java.time.LocalDateTime;

/** The message published to Kafka (as JSON). */
public record DomainEvent(
        String eventType,        // EVENT_CREATED | ATTENDEE_REGISTERED | EVENT_CANCELLED
        Long eventId,
        String eventName,
        String attendeeName,
        String attendeeEmail,
        LocalDateTime occurredAt) {
}
