package bbattulga.matchengine.serviceengineconsumer;

import bbattulga.matchengine.libmodel.engine.output.EngineOutput;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.serviceengineconsumer.config.EngineConsumerConfigProps;
import bbattulga.matchengine.serviceengineconsumer.config.RabbitMQConfig;
import bbattulga.matchengine.serviceengineconsumer.consumer.EngineOutputConsumerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EngineOutputConsumer {

    private final EngineOutputConsumerService engineOutputConsumerService;
    private final EngineConsumerConfigProps config;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ENGINE_OUT, concurrency = "1", exclusive = true, containerFactory = "engineOutputRabbitListenerContainerFactory")
    public void consume(String message) throws JsonProcessingException, BadParameterException {
        if (!config.isEnabled()) {
            log.warn("Engine Consumer is not enabled");
            return;
        }
        //  By default, all failed messages will be immediately requeued at the head of the target queue over and over again.
        // https://www.baeldung.com/spring-amqp-error-handling
        EngineOutput output = objectMapper.readValue(message, EngineOutput.class);
        engineOutputConsumerService.consume(output);
        log.info("{}", output.getUtc());
    }
}
