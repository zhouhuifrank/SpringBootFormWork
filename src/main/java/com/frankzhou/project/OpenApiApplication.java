package com.frankzhou.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableTransactionManagement
@EnableSwagger2
@SpringBootApplication
@MapperScan("com.frankzhou.project.mapper")
public class OpenApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenApiApplication.class, args);
		System.out.println("================================================");
		System.out.println("=============Spring Boot Start Succeed==========");
		System.out.println("=================================================");
	}

}
