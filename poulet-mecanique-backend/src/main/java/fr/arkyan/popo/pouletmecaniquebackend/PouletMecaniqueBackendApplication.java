package fr.arkyan.popo.pouletmecaniquebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PouletMecaniqueBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PouletMecaniqueBackendApplication.class, args);
	}

}
