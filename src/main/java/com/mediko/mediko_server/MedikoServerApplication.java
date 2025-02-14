package com.mediko.mediko_server;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
public class MedikoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedikoServerApplication.class, args);
	}

}
