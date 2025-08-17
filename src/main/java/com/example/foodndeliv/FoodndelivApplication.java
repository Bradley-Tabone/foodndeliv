package com.example.foodndeliv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class FoodndelivApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodndelivApplication.class, args);
	}

}
