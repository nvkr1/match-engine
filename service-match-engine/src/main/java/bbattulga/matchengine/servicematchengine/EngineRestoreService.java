package bbattulga.matchengine.servicematchengine;

import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.engine.LimitOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.libmodel.exception.AssetNotFoundException;
import bbattulga.matchengine.libmodel.exception.PairNotFoundException;
import bbattulga.matchengine.libmodel.jpa.entity.Asset;
import bbattulga.matchengine.libmodel.jpa.entity.Pair;
import bbattulga.matchengine.libmodel.jpa.repository.AssetRepository;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import bbattulga.matchengine.libmodel.jpa.repository.PairRepository;
import bbattulga.matchengine.servicematchengine.config.MatchEngineConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EngineRestoreService {

    private final MatchEngineConfig config;
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final PairRepository pairRepository;
    private final LimitOrderExecutorService limitOrderExecutorService;
    private final CancelOrderExecutorService cancelOrderExecutorService;

    @PostConstruct
    public void restoreOrders() throws JsonProcessingException {
        final var base = assetRepository.findBySymbolAndStatus(config.getBase(), Asset.Status.ACTIVE).orElseThrow(AssetNotFoundException::new);
        final var quote = assetRepository.findBySymbolAndStatus(config.getQuote(), Asset.Status.ACTIVE).orElseThrow(AssetNotFoundException::new);
        final var pair = pairRepository.findByBaseAssetIdAndQuoteAssetIdAndStatus(base.getAssetId(), quote.getAssetId(), Pair.Status.ACTIVE).orElseThrow(PairNotFoundException::new);
        // TODO:: paginate
        final var orders = orderRepository.findByPairIdAndStatusInOrderByUtcAsc(pair.getPairId(), List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED));
        for (final var order: orders) {
            final var event = new OrderEvent();
            event.setId(order.getOrderId().toString());
            event.setUid(order.getUid().toString());
            event.setType(order.getType());
            event.setSide(order.getSide());
            event.setPrice(order.getPrice());
            event.setQty(order.getQty());
            event.setTotal(order.getTotal());
            event.setRemainingQty(order.getRemainingQty());
            event.setRemainingTotal(order.getRemainingTotal());
            event.setExecQty(order.getExecQty());
            event.setExecTotal(order.getExecTotal());
            event.setFillQty(order.getFillQty());
            event.setFillTotal(order.getFillTotal());
            event.setStatus(order.getStatus());
            event.setUtc(order.getUtc());
            if (event.getType().equals(OrderType.LIMIT)) {
                limitOrderExecutorService.executeLimitOrder(event);
            } else if (event.getType().equals(OrderType.CANCEL)) {
                cancelOrderExecutorService.executeCancelOrder(event);
            }
        }
    }
}
