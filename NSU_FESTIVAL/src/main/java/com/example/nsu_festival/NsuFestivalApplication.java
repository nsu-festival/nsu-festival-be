package com.example.nsu_festival;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class NsuFestivalApplication {

	public static void main(String[] args) {
		SpringApplication.run(NsuFestivalApplication.class, args);
	}

}
