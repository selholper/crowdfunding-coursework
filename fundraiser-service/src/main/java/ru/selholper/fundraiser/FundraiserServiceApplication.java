package ru.selholper.fundraiser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FundraiserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundraiserServiceApplication.class, args);
	}

}