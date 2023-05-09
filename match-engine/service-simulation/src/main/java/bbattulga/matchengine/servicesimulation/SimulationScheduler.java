package bbattulga.matchengine.servicesimulation;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulationScheduler {

    @Scheduled(fixedDelay = 60_000)
    public void runAtInterval() {

    }
}
