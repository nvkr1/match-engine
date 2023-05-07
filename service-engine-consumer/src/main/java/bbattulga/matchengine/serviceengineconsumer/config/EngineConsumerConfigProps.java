package bbattulga.matchengine.serviceengineconsumer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "service-engine-consumer")
@Getter
@Setter
public class EngineConsumerConfigProps {
    private boolean enabled;
}
