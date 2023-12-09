package com.event.microservice.repositories;

import java.time.LocalDateTime;

import com.event.microservice.entities.EventStore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface EventRepository extends JpaRepository<EventStore, Long>{

	Iterable<EventStore> findByEventOf(String eventOf);
	
	Iterable<EventStore> findByEventOfAndEventTimeLessThanEqual(String eventOf,LocalDateTime date);
}

