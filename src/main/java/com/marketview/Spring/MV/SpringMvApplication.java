package com.marketview.Spring.MV;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //used to execute transactions and if they fail partially everything would rollback!
public class SpringMvApplication {


	@Value("${spring.data.mongodb.uri}")
	private String mongoUri;

	public static void main(String[] args) {

		SpringApplication.run(SpringMvApplication.class, args);
	}
	@PostConstruct
	public void printConfig() {
		System.out.println("Mongo URI: " + mongoUri);
	}

}
