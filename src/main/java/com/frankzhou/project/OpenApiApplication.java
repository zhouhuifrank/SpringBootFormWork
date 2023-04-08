package com.frankzhou.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenApiApplication.class, args);
		System.out.println("================================================");
		System.out.println("===============Spring Boot启动成功================");
		System.out.println("=================================================");
	}

}
