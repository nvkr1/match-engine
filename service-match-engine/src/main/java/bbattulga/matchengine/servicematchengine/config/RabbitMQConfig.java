package bbattulga.matchengine.servicematchengine.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TOPIC_ENGINE_OUT = "engine_output.topic";

    @Bean
    TopicExchange engineOutExchange() {
        return new TopicExchange(TOPIC_ENGINE_OUT, true, false);
    }

}
