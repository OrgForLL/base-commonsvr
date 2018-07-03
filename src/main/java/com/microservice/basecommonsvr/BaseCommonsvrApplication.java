package com.microservice.basecommonsvr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BaseCommonsvrApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseCommonsvrApplication.class, args);
	}
}
