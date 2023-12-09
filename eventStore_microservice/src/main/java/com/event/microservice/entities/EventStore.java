package com.event.microservice.entities;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data

public class EventStore {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long eventId;

	private String eventType;

	private String eventOf;
	
	private String eventData;

	private LocalDateTime eventTime;

}
