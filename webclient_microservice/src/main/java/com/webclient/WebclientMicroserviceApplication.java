package com.webclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class WebclientMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebclientMicroserviceApplication.class, args);
	}

}
