package com.market_view.spring.mv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class SpringMvApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringMvApplication.class, args);
	}

}
