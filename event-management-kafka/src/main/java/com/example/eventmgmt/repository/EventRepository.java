package com.example.eventmgmt.repository;

import com.example.eventmgmt.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
