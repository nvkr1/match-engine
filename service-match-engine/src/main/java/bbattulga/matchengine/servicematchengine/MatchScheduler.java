package bbattulga.matchengine.servicematchengine;

import bbattulga.matchengine.servicematchengine.service.MatchService;
import bbattulga.matchengine.servicematchengine.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchScheduler {

    private final QueueService queueService;
    private final MatchService matchService;

    @Scheduled(fixedDelay = 1)
    public void matchLimitAtInterval() throws Exception {
        final var orderOpt = queueService.takeOrder();
        if (orderOpt.isEmpty()) {
            return;
        }
        final var order = orderOpt.get();
        matchService.placeLimitOrder(order);
    }
}
