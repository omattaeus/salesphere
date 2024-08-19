package com.salesphere.salesphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.salesphere.salesphere")
@EnableScheduling
public class Startup {

	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);
	}
}