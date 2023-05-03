package bbattulga.matchengine.serviceuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"bbattulga.matchengine.serviceuser",
		"bbattulga.matchengine.libservice.orderlog",
}, exclude = {
		// TODO:: Authentication
		SecurityAutoConfiguration.class
})
@EntityScan({
		"bbattulga.matchengine.libmodel.jpa.entity"
})
@EnableJpaRepositories({
		"bbattulga.matchengine.libmodel.jpa.repository"
})
public class ServiceUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceUserApplication.class, args);
	}

}
