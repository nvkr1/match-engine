package bbattulga.matchengine.serviceengineconsumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TOPIC_ENGINE_OUT = "engine_output.topic";
    public static final String QUEUE_ENGINE_OUT = "engine_output.q";

    @Bean
    Queue outputQueue() {
        return new Queue(QUEUE_ENGINE_OUT, true, false, false);
    }

    @Bean
    TopicExchange engineOutExchange() {
        return new TopicExchange(TOPIC_ENGINE_OUT, true, false);
    }

    @Bean
    Binding engineOutputBinding(Queue outputQueue, TopicExchange engineOutExchange) {
        return BindingBuilder.bind(outputQueue).to(engineOutExchange).with("*.*.*");
    }
}
