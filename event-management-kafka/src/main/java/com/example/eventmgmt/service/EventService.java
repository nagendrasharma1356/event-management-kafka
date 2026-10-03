package com.example.eventmgmt.service;

import com.example.eventmgmt.dto.CreateEventRequest;
import com.example.eventmgmt.dto.DomainEvent;
import com.example.eventmgmt.dto.RegisterAttendeeRequest;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.entity.EventStatus;
import com.example.eventmgmt.entity.Registration;
import com.example.eventmgmt.kafka.EventProducer;
import com.example.eventmgmt.repository.EventRepository;
import com.example.eventmgmt.repository.RegistrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepo;
    private final RegistrationRepository registrationRepo;
    private final EventProducer producer;

    public EventService(EventRepository eventRepo, RegistrationRepository registrationRepo, EventProducer producer) {
        this.eventRepo = eventRepo;
        this.registrationRepo = registrationRepo;
        this.producer = producer;
    }

    public Event create(CreateEventRequest req) {
        Event e = new Event();
        e.setName(req.name());
        e.setVenue(req.venue());
        e.setEventDate(req.eventDate());
        e.setCapacity(req.capacity());
        e = eventRepo.save(e);

        producer.publish(new DomainEvent("EVENT_CREATED", e.getId(), e.getName(), null, null, LocalDateTime.now()));
        return e;
    }

    public List<Event> findAll() {
        return eventRepo.findAll();
    }

    public Event get(Long id) {
        return eventRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + id));
    }

    @Transactional
    public Registration register(Long eventId, RegisterAttendeeRequest req) {
        Event event = get(eventId);
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is cancelled");
        }
        if (registrationRepo.existsByEventIdAndAttendeeEmail(eventId, req.attendeeEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already registered: " + req.attendeeEmail());
        }
        if (registrationRepo.countByEventId(eventId) >= event.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is full");
        }

        Registration r = new Registration();
        r.setEventId(eventId);
        r.setAttendeeName(req.attendeeName());
        r.setAttendeeEmail(req.attendeeEmail());
        r = registrationRepo.save(r);

        producer.publish(new DomainEvent("ATTENDEE_REGISTERED", eventId, event.getName(),
                r.getAttendeeName(), r.getAttendeeEmail(), LocalDateTime.now()));
        return r;
    }

    public List<Registration> registrations(Long eventId) {
        get(eventId);
        return registrationRepo.findByEventId(eventId);
    }

    @Transactional
    public Event cancel(Long eventId) {
        Event event = get(eventId);
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event already cancelled");
        }
        event.setStatus(EventStatus.CANCELLED);
        event = eventRepo.save(event);

        producer.publish(new DomainEvent("EVENT_CANCELLED", eventId, event.getName(), null, null, LocalDateTime.now()));
        return event;
    }
}
