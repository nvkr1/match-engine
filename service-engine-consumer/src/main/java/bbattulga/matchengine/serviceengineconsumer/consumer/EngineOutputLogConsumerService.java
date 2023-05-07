package bbattulga.matchengine.serviceengineconsumer.consumer;

import bbattulga.matchengine.libmodel.engine.output.EngineOutput;
import bbattulga.matchengine.libmodel.jpa.entity.EngineOutputLog;
import bbattulga.matchengine.libmodel.jpa.repository.EngineOutputLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EngineOutputLogConsumerService {

    private final EngineOutputLogRepository engineOutputLogRepository;

    @Transactional
    public void consume(EngineOutput output) {
        final var log = new EngineOutputLog();
        log.setType(output.getType().toString());
        log.setUtc(output.getUtc());
        log.setPayload(output.getPayload());
        log.setCreatedAt(LocalDateTime.now());
        engineOutputLogRepository.save(log);
    }
}
