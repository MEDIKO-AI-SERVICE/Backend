package com.mediko.mediko_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MedikoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedikoServerApplication.class, args);
	}

}
