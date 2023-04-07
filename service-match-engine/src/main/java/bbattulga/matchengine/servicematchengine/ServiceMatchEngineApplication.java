package bbattulga.matchengine.servicematchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"bbattulga.matchengine.servicematchengine",
		"bbattulga.matchengine.libmodel"
})
public class ServiceMatchEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceMatchEngineApplication.class, args);
	}

}
