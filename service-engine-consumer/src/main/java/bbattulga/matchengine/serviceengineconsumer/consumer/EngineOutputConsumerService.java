package bbattulga.matchengine.serviceengineconsumer.consumer;

import bbattulga.matchengine.libmodel.engine.output.EngineOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderCancelOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderMatchOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderOpenOutput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EngineOutputConsumerService {

    private final ObjectMapper objectMapper;
    private final MatchConsumer matchConsumer;
    private final OrderCancelConsumer orderCancelConsumer;
    private final OrderOpenConsumer orderOpenConsumer;
    private final EngineOutputLogConsumerService engineOutputLogConsumerService;

    @Transactional
    public void consume(EngineOutput output) throws JsonProcessingException {
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
        engineOutputLogConsumerService.consume(output);
    }

}
