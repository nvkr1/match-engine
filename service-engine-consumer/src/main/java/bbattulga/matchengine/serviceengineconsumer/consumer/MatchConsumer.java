package bbattulga.matchengine.serviceengineconsumer.consumer;

import bbattulga.matchengine.libmodel.engine.output.OrderMatchOutput;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.jpa.entity.Asset;
import bbattulga.matchengine.libmodel.jpa.entity.Match;
import bbattulga.matchengine.libmodel.jpa.entity.Pair;
import bbattulga.matchengine.libmodel.jpa.repository.AssetRepository;
import bbattulga.matchengine.libmodel.jpa.repository.MatchRepository;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import bbattulga.matchengine.libmodel.jpa.repository.PairRepository;
import bbattulga.matchengine.libservice.orderlog.OrderLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchConsumer {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final PairRepository pairRepository;
    private final MatchRepository matchRepository;
    private final OrderLogService orderLogService;

    @Transactional
    public void consume(OrderMatchOutput matchOutput) throws BadParameterException {
        final var baseAsset = assetRepository.findBySymbolAndStatus(matchOutput.getBase(), Asset.Status.ACTIVE).orElseThrow(() -> new BadParameterException("asset-not-found"));
        final var quoteAsset = assetRepository.findBySymbolAndStatus(matchOutput.getQuote(), Asset.Status.ACTIVE).orElseThrow(() -> new BadParameterException("asset-not-found"));
        final var pair = pairRepository.findByBaseAssetIdAndQuoteAssetIdAndStatus(baseAsset.getAssetId(), quoteAsset.getAssetId(), Pair.Status.ACTIVE).orElseThrow(() -> new BadParameterException("pair-not-found"));
        final var execOrder = orderRepository.findById(UUID.fromString(matchOutput.getExecOrder().getId())).orElseThrow(() -> new BadParameterException("exec-order-not-found"));
        final var remainingOrder = orderRepository.findById(UUID.fromString(matchOutput.getRemainingOrder().getId())).orElseThrow(() -> new BadParameterException("remaining-order-not-found"));
        execOrder.setUpdatedAt(LocalDateTime.now());
        execOrder.setStatus(matchOutput.getExecOrder().getStatus());
        execOrder.setPrice(matchOutput.getExecOrder().getPrice());
        execOrder.setQty(matchOutput.getExecOrder().getQty());
        execOrder.setTotal(matchOutput.getExecOrder().getTotal());
        execOrder.setExecQty(matchOutput.getExecOrder().getExecQty());
        execOrder.setExecTotal(matchOutput.getExecOrder().getExecTotal());
        execOrder.setRemainingQty(matchOutput.getExecOrder().getRemainingQty());
        execOrder.setRemainingTotal(matchOutput.getExecOrder().getRemainingTotal());
        orderRepository.save(execOrder);
        orderLogService.saveOrderLog(execOrder);
        remainingOrder.setUpdatedAt(LocalDateTime.now());
        remainingOrder.setStatus(matchOutput.getRemainingOrder().getStatus());
        remainingOrder.setPrice(matchOutput.getRemainingOrder().getPrice());
        remainingOrder.setQty(matchOutput.getRemainingOrder().getQty());
        remainingOrder.setTotal(matchOutput.getRemainingOrder().getTotal());
        remainingOrder.setFillQty(matchOutput.getRemainingOrder().getFillQty());
        remainingOrder.setFillTotal(matchOutput.getRemainingOrder().getFillTotal());
        remainingOrder.setRemainingQty(matchOutput.getRemainingOrder().getRemainingQty());
        remainingOrder.setRemainingTotal(matchOutput.getRemainingOrder().getRemainingTotal());
        orderRepository.save(remainingOrder);
        orderLogService.saveOrderLog(remainingOrder);
        final var match = new Match();
        match.setExecOrderId(execOrder.getOrderId());
        match.setRemainingOrderId(remainingOrder.getOrderId());
        match.setMakerFee(matchOutput.getMakerFee());
        match.setTakerFee(matchOutput.getTakerFee());
        match.setPrice(matchOutput.getPrice());
        match.setQty(matchOutput.getQty());
        match.setTotal(matchOutput.getTotal());
        match.setPairId(pair.getPairId());
        match.setBaseAssetId(baseAsset.getAssetId());
        match.setQuoteAssetId(quoteAsset.getAssetId());
        match.setUtc(matchOutput.getUtc());
        match.setCreatedAt(LocalDateTime.now());
        matchRepository.save(match);

        // update pair last price
        pair.setLastPrice(match.getPrice());
        pair.setUpdatedAt(LocalDateTime.now());
        pairRepository.save(pair);
    }
}
