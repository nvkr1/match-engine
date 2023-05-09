package bbattulga.matchengine.servicematchengine;

import bbattulga.matchengine.libmodel.engine.output.OrderCancelOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderMatchOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderOpenOutput;
import bbattulga.matchengine.libmodel.engine.output.OutputEvent;
import bbattulga.matchengine.servicematchengine.service.output.OutputRabbitMQService;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadFactory;

@Service
@RequiredArgsConstructor
public class SequentialOutputService {

    private RingBuffer<OutputEvent> outputEventRingBuffer;
    private final OutputRabbitMQService outputRabbitMQService;

    @PostConstruct
    private void initOutputRingBuffer() {
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        WaitStrategy waitStrategy = new BusySpinWaitStrategy();
        Disruptor<OutputEvent> disruptor
                = new Disruptor<>(
                OutputEvent.EVENT_FACTORY,
                (int) Math.pow(2, 18),
                threadFactory,
                ProducerType.SINGLE,
                waitStrategy);
        disruptor.handleEventsWith(outputEventHandler());
        outputEventRingBuffer = disruptor.start();
    }

    public void publish(OutputEvent output) {
        final var sequenceId = this.outputEventRingBuffer.next();
        final var event =  this.outputEventRingBuffer.get(sequenceId);
        event.setOrderId(output.getOrderId());
        event.setOutputType(output.getOutputType());
        event.setType(output.getType());
        event.setUid(output.getUid());
        event.setPrice(output.getPrice());
        event.setQty(output.getQty());
        event.setTotal(output.getTotal());
        event.setExecQty(output.getExecQty());
        event.setExecTotal(output.getExecTotal());
        event.setFillQty(output.getFillQty());
        event.setFillTotal(output.getFillTotal());
        event.setRemainingQty(output.getRemainingQty());
        event.setRemainingTotal(output.getRemainingTotal());
        event.setExecOrder(output.getExecOrder());
        event.setRemainingOrder(output.getRemainingOrder());
        event.setBase(output.getBase());
        event.setQuote(output.getQuote());
        event.setSide(output.getSide());
        event.setExecUtc(output.getExecUtc());
        event.setUtc(output.getUtc());
        event.setNs(output.getNs());
        event.setStatus(output.getStatus());
        event.setMakerFee(output.getMakerFee());
        event.setTakerFee(output.getTakerFee());
        this.outputEventRingBuffer.publish(sequenceId);
    }

    private EventHandler<OutputEvent>[] outputEventHandler() {
        final EventHandler<OutputEvent> eventHandler = (outputEvent, sequence, endOfBatch) -> {
            switch (outputEvent.getOutputType()) {
                case OPEN -> outputRabbitMQService.publishOpen(outputEvent);
                case MATCH -> outputRabbitMQService.publishMatch(outputEvent);
                case CANCEL -> outputRabbitMQService.publishCancel(outputEvent);
                default -> throw new Exception("Invalid output type");
            }
        };
        return new EventHandler[] { eventHandler };
    }
}
