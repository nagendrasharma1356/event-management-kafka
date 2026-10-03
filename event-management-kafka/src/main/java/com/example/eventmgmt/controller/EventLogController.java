package com.example.eventmgmt.controller;

import com.example.eventmgmt.entity.EventLog;
import com.example.eventmgmt.repository.EventLogRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Shows what the Kafka consumer received and stored - proof the producer -> topic -> consumer loop works. */
@RestController
@RequestMapping("/api/event-logs")
public class EventLogController {

    private final EventLogRepository repo;

    public EventLogController(EventLogRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<EventLog> all() {
        return repo.findAllByOrderByIdDesc();
    }
}
