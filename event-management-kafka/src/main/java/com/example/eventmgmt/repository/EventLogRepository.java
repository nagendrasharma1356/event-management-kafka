package com.example.eventmgmt.repository;

import com.example.eventmgmt.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    boolean existsByKafkaPartitionAndKafkaOffset(int kafkaPartition, long kafkaOffset);
    List<EventLog> findAllByOrderByIdDesc();
}
