package com.webclient.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Beans {
	
	@Bean
	WebClient webClient() {

		return WebClient.builder().baseUrl("http://localhost:9070").build();
	}

}
