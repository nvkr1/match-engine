package bbattulga.matchengine.servicematchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"bbattulga.matchengine.libservice.controlleradvice",
		"bbattulga.matchengine.servicematchengine",
})
@EnableJpaRepositories({
		"bbattulga.matchengine.libmodel.jpa.repository"
})
@EntityScan({
		"bbattulga.matchengine.libmodel.jpa.entity"
})
@EnableDiscoveryClient
public class ServiceMatchEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceMatchEngineApplication.class, args);
	}

}
