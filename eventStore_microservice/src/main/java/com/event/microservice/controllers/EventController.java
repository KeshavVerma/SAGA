package com.event.microservice.controllers;

import com.event.microservice.entities.EventStore;
import com.event.microservice.services.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@GetMapping("/events")
	public Iterable<EventStore> getEvents(@RequestParam("name") String name) throws JsonProcessingException {

		Iterable<EventStore> events = eventService.fetchAllEvents(name);

		return events;

	}

}
