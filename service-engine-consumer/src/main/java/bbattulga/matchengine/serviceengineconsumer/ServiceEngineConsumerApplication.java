package bbattulga.matchengine.serviceengineconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan({
	"bbattulga.matchengine.libmodel.jpa.entity"
})
@EnableJpaRepositories({
		"bbattulga.matchengine.libmodel.jpa.repository"
})
public class ServiceEngineConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceEngineConsumerApplication.class, args);
	}

}
