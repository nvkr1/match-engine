package bbattulga.matchengine.servicesimulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServiceSimulationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceSimulationApplication.class, args);
	}

}
