package com.example.eventmgmt.kafka;

import com.example.eventmgmt.dto.DomainEvent;
import com.example.eventmgmt.entity.EventLog;
import com.example.eventmgmt.repository.EventLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    private final EventLogRepository logRepo;
    private final ObjectMapper objectMapper;

    public EventConsumer(EventLogRepository logRepo, ObjectMapper objectMapper) {
        this.logRepo = logRepo;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topic}")
    public void onMessage(ConsumerRecord<String, String> record) throws Exception {
        // Kafka is at-least-once: the same record can be delivered twice, so skip what we already stored.
        if (logRepo.existsByKafkaPartitionAndKafkaOffset(record.partition(), record.offset())) {
            return;
        }

        DomainEvent event = objectMapper.readValue(record.value(), DomainEvent.class);

        EventLog entry = new EventLog();
        entry.setEventType(event.eventType());
        entry.setMessageKey(record.key());
        entry.setKafkaPartition(record.partition());
        entry.setKafkaOffset(record.offset());
        entry.setPayload(record.value());
        logRepo.save(entry);

        // Simulated downstream work (in a real system: email service, analytics, etc.)
        switch (event.eventType()) {
            case "EVENT_CREATED" ->
                    log.info("[notify] New event announced: '{}'", event.eventName());
            case "ATTENDEE_REGISTERED" ->
                    log.info("[notify] Sending confirmation email to {} for '{}'", event.attendeeEmail(), event.eventName());
            case "EVENT_CANCELLED" ->
                    log.info("[notify] Event '{}' cancelled - informing all attendees", event.eventName());
            default -> log.info("[notify] Unhandled event type {}", event.eventType());
        }
    }
}
