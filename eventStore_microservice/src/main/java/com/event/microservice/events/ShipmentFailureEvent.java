package com.event.microservice.events;

import com.event.microservice.dto.Shipment;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShipmentFailureEvent {
	
	private Shipment shipmentDetails;
	
}
