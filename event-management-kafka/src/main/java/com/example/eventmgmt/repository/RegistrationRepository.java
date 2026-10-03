package com.example.eventmgmt.repository;

import com.example.eventmgmt.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);
    long countByEventId(Long eventId);
    boolean existsByEventIdAndAttendeeEmail(Long eventId, String attendeeEmail);
}
