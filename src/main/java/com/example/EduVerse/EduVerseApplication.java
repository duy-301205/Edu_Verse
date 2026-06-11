package com.example.EduVerse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EduVerseApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduVerseApplication.class, args);
	}

}
