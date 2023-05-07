package bbattulga.matchengine.servicematchengine.config;

import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.servicematchengine.SequentialExecutionService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadFactory;

@Configuration
@RequiredArgsConstructor
public class RingBufferConfig {

    private final SequentialExecutionService sequentialExecutionService;

    @Bean
    RingBuffer<OrderEvent> orderEventRingBuffer() {
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        WaitStrategy waitStrategy = new BusySpinWaitStrategy();
        Disruptor<OrderEvent> disruptor
                = new Disruptor<>(
                OrderEvent.EVENT_FACTORY,
                (int) Math.pow(2, 15),
                threadFactory,
                ProducerType.MULTI,
                waitStrategy);
        disruptor.handleEventsWith(sequentialExecutionService.getEventHandler());
        return disruptor.start();
    }

    @Bean
    ObjectMapper objectMapper() {
        final var objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
}
