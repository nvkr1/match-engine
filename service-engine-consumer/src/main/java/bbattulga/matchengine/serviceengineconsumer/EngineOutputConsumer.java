package bbattulga.matchengine.serviceengineconsumer;

import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.engine.output.EngineOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderCancelOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderMatchOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderOpenOutput;
import bbattulga.matchengine.serviceengineconsumer.config.RabbitMQConfig;
import bbattulga.matchengine.serviceengineconsumer.consumer.OrderCancelConsumer;
import bbattulga.matchengine.serviceengineconsumer.consumer.MatchConsumer;
import bbattulga.matchengine.serviceengineconsumer.consumer.OrderOpenConsumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EngineOutputConsumer {

    private final ObjectMapper objectMapper;
    private final MatchConsumer matchConsumer;
    private final OrderCancelConsumer orderCancelConsumer;
    private final OrderOpenConsumer orderOpenConsumer;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ENGINE_OUT)
    public void consume(String message) throws JsonProcessingException, BadParameterException {
        //  By default, all failed messages will be immediately requeued at the head of the target queue over and over again.
        // https://www.baeldung.com/spring-amqp-error-handling
        EngineOutput output = objectMapper.readValue(message, EngineOutput.class);
        switch (output.getType()) {
            case MATCH:
                final var match = objectMapper.readValue(output.getPayload(), OrderMatchOutput.class);
                matchConsumer.consume(match);
                break;
            case OPEN:
                final var open = objectMapper.readValue(output.getPayload(), OrderOpenOutput.class);
                orderOpenConsumer.consume(open);
                break;
            case CANCEL:
                final var cancel = objectMapper.readValue(output.getPayload(), OrderCancelOutput.class);
                orderCancelConsumer.consume(cancel);
                break;
            default:
                break;
        }
    }
}
