package bbattulga.matchengine.servicematchengine.config;

import bbattulga.matchengine.servicematchengine.OrderEventHandler;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
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
public class BeanConfig {

    private final OrderEventHandler orderEventHandler;

    @Bean
    RingBuffer<OrderEvent> orderEventDisruptor() {
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        WaitStrategy waitStrategy = new BusySpinWaitStrategy();
        Disruptor<OrderEvent> disruptor
                = new Disruptor<>(
                OrderEvent.EVENT_FACTORY,
                2*8,
                threadFactory,
                ProducerType.MULTI,
                waitStrategy);
        disruptor.handleEventsWith(orderEventHandler.getEventHandler());
        return disruptor.start();
    }
}
