package com.exm.flipkartclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class FlipkartCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlipkartCloneApplication.class, args);
		log.info("Hello Flipkart");
		System.out.println("Hello Flipkart");

		
	}

}
