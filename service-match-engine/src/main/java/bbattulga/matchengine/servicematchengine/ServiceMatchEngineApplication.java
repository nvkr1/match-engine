package bbattulga.matchengine.servicematchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {
		"bbattulga.matchengine.libservice.controlleradvice",
		"bbattulga.matchengine.servicematchengine",
})
@EnableDiscoveryClient
public class ServiceMatchEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceMatchEngineApplication.class, args);
	}

}
