package bbattulga.matchengine.serviceuser.controller;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.jpa.entity.Asset;
import bbattulga.matchengine.libmodel.jpa.entity.Pair;
import bbattulga.matchengine.libmodel.jpa.repository.AssetRepository;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import bbattulga.matchengine.libmodel.jpa.repository.PairRepository;
import bbattulga.matchengine.serviceuser.dto.response.OrderBookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderBookController {

    private final OrderRepository orderRepository;
    private final PairRepository pairRepository;
    private final AssetRepository assetRepository;

    @GetMapping("/order-book/{pairId}")
    public OrderBookResponse orderBook(@PathVariable Long pairId) {
        final var pair = pairRepository.findByPairIdAndStatus(pairId, Pair.Status.ACTIVE).orElseThrow(() -> new BadParameterException("pair-not-found"));
        final var base = assetRepository.findByAssetIdAndStatus(pair.getBaseAssetId(), Asset.Status.ACTIVE).orElseThrow(() -> new BadParameterException("asset-not-found"));
        final var quote = assetRepository.findByAssetIdAndStatus(pair.getQuoteAssetId(), Asset.Status.ACTIVE).orElseThrow(() -> new BadParameterException("asset-not-found"));
        final var orderBook = orderRepository.findOrderBookByPairId(pair.getPairId());
        List<OrderBookResponse.Depth> ask = new ArrayList<>();
        List<OrderBookResponse.Depth> bid = new ArrayList<>();
        orderBook.forEach((level) -> {
            final var realPrice = level.getPrice().scaleByPowerOfTen(-1*quote.getScale());
            final var realQty = level.getQty().scaleByPowerOfTen(-1*base.getScale());
            final var total = level.getPrice().multiply(realQty);
            final var realTotal = total.scaleByPowerOfTen(-1*quote.getScale());
            final var responsePrice = realPrice.setScale(pair.getQuoteDecimal(), RoundingMode.CEILING);
            final var responseQty = realQty.setScale(pair.getBaseDecimal(), RoundingMode.CEILING);
            final var responseTotal = realTotal.setScale(pair.getQuoteDecimal(), RoundingMode.CEILING);
            final var responseDepth = OrderBookResponse.Depth.builder()
                    .side(level.getSide())
                    .price(responsePrice.stripTrailingZeros().toPlainString())
                    .qty(responseQty.stripTrailingZeros().toPlainString())
                    .total(responseTotal.stripTrailingZeros().toPlainString())
                    .build();
            if (responseDepth.getSide().equals(OrderSide.BUY)) {
                bid.add(responseDepth);
            } else if (responseDepth.getSide().equals(OrderSide.SELL)) {
                ask.add(responseDepth);
            }
        });
        return OrderBookResponse.builder()
                .ask(ask)
                .bid(bid)
                .build();
    }
}
