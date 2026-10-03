package com.example.eventmgmt.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** One row per Kafka message the consumer processed (an audit trail of what came through the topic). */
@Entity
@Table(name = "event_logs")
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;
    private String messageKey;
    private int kafkaPartition;
    private long kafkaOffset;

    @Column(length = 4000)
    private String payload;

    private LocalDateTime receivedAt;

    @PrePersist
    void onCreate() { this.receivedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getMessageKey() { return messageKey; }
    public void setMessageKey(String messageKey) { this.messageKey = messageKey; }
    public int getKafkaPartition() { return kafkaPartition; }
    public void setKafkaPartition(int kafkaPartition) { this.kafkaPartition = kafkaPartition; }
    public long getKafkaOffset() { return kafkaOffset; }
    public void setKafkaOffset(long kafkaOffset) { this.kafkaOffset = kafkaOffset; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
}
