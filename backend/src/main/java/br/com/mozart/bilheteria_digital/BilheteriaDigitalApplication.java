package br.com.mozart.bilheteria_digital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BilheteriaDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BilheteriaDigitalApplication.class, args);
	}

}
