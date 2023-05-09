package bbattulga.matchengine.serviceuser.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "service-user")
@Getter
@Setter
public class ServiceUserConfig {
    private String matchEngineUrl;
}
