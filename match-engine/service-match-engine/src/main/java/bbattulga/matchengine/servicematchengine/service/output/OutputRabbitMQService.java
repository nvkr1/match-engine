package bbattulga.matchengine.servicematchengine.service.output;

import bbattulga.matchengine.libmodel.consts.OutputType;
import bbattulga.matchengine.libmodel.engine.output.EngineOutput;
import bbattulga.matchengine.libmodel.engine.output.IOrderCancelOutput;
import bbattulga.matchengine.libmodel.engine.output.IOrderMatchOutput;
import bbattulga.matchengine.libmodel.engine.output.IOrderOpenOutput;
import bbattulga.matchengine.servicematchengine.config.RabbitMQConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OutputRabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void setObjectMapper() {
        objectMapper = new ObjectMapper();
    }

    public void publishMatch(IOrderMatchOutput output) throws JsonProcessingException {
        final var engineOut = EngineOutput.builder()
                .type(OutputType.MATCH)
                .utc(Instant.now().toEpochMilli())
                .payload(objectMapper.writeValueAsString(output))
                .build();
        final var routingKey = String.format("match.%s.%s", output.getBase(), output.getQuote());
        final var msg = objectMapper.writeValueAsString(engineOut);
        boolean isPublished = false;
        do {
            try {
                rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_ENGINE_OUT, routingKey, msg);
                isPublished = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (!isPublished);
    }

    public void publishOpen(IOrderOpenOutput output) throws JsonProcessingException {
        final var engineOut = EngineOutput.builder()
                .type(OutputType.OPEN)
                .utc(Instant.now().toEpochMilli())
                .payload(objectMapper.writeValueAsString(output))
                .build();
        final var routingKey = String.format("open.%s.%s", output.getBase(), output.getQuote());
        final var msg = objectMapper.writeValueAsString(engineOut);
        boolean isPublished = false;
        do {
            try {
                rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_ENGINE_OUT, routingKey, msg);
                isPublished = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (!isPublished);
    }

    public void publishCancel(IOrderCancelOutput output) throws JsonProcessingException {
        final var engineOut = EngineOutput.builder()
                .type(OutputType.CANCEL)
                .utc(Instant.now().toEpochMilli())
                .payload(objectMapper.writeValueAsString(output))
                .build();
        final var routingKey = String.format("cancel.%s.%s", output.getBase(), output.getQuote());
        final var msg = objectMapper.writeValueAsString(engineOut);
        boolean isPublished = false;
        do {
            try {
                rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_ENGINE_OUT, routingKey, msg);
                isPublished = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (!isPublished);
    }
}
