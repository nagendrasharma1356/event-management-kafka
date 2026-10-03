package com.example.eventmgmt.controller;

import com.example.eventmgmt.dto.CreateEventRequest;
import com.example.eventmgmt.dto.RegisterAttendeeRequest;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.Registration;
import com.example.eventmgmt.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody CreateEventRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<Event> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Event one(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/{id}/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Registration register(@PathVariable Long id, @Valid @RequestBody RegisterAttendeeRequest req) {
        return service.register(id, req);
    }

    @GetMapping("/{id}/registrations")
    public List<Registration> registrations(@PathVariable Long id) {
        return service.registrations(id);
    }

    @PostMapping("/{id}/cancel")
    public Event cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
