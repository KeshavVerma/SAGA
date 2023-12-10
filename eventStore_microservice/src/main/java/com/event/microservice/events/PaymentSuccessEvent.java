package com.event.microservice.events;

import com.event.microservice.dto.Payment;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentSuccessEvent {
	
	private Payment paymentDetails;
	
}
