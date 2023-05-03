package bbattulga.matchengine.servicematchengine.service.output;

import bbattulga.matchengine.libmodel.consts.OutputType;
import bbattulga.matchengine.libmodel.engine.output.EngineOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderCancelOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderMatchOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderOpenOutput;
import bbattulga.matchengine.servicematchengine.config.RabbitMQConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EngineOutputPublisherService {

    private final RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void setObjectMapper() {
        objectMapper = new ObjectMapper();
    }


    public void publish(OrderMatchOutput output) throws JsonProcessingException {
        final var engineOut = EngineOutput.builder()
                .type(OutputType.MATCH)
                .payload(objectMapper.writeValueAsString(output))
                .build();
        final var routingKey = String.format("match.%s.%s", output.getBase(), output.getQuote());
        final var msg = objectMapper.writeValueAsString(engineOut);
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_ENGINE_OUT, routingKey, msg);
    }

    public void publish(OrderOpenOutput output) throws JsonProcessingException {
        final var engineOut = EngineOutput.builder()
                .type(OutputType.OPEN)
                .payload(objectMapper.writeValueAsString(output))
                .build();
        final var routingKey = String.format("open.%s.%s", output.getBase(), output.getQuote());
        final var msg = objectMapper.writeValueAsString(engineOut);
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_ENGINE_OUT, routingKey, msg);
    }

    public void publish(OrderCancelOutput output) throws JsonProcessingException {
        final var engineOut = EngineOutput.builder()
                .type(OutputType.CANCEL)
                .payload(objectMapper.writeValueAsString(output))
                .build();
        final var routingKey = String.format("cancel.%s.%s", output.getBase(), output.getQuote());
        final var msg = objectMapper.writeValueAsString(engineOut);
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_ENGINE_OUT, routingKey, msg);
    }
}
