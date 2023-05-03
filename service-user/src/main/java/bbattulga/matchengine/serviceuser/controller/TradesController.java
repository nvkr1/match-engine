package bbattulga.matchengine.serviceuser.controller;

import bbattulga.matchengine.libmodel.exception.AssetNotFoundException;
import bbattulga.matchengine.libmodel.exception.PairNotFoundException;
import bbattulga.matchengine.libmodel.jpa.entity.Asset;
import bbattulga.matchengine.libmodel.jpa.entity.Pair;
import bbattulga.matchengine.libmodel.jpa.repository.AssetRepository;
import bbattulga.matchengine.libmodel.jpa.repository.MatchRepository;
import bbattulga.matchengine.libmodel.jpa.repository.PairRepository;
import bbattulga.matchengine.serviceuser.dto.response.RecentTradesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TradesController {

    private final MatchRepository matchRepository;
    private final PairRepository pairRepository;
    private final AssetRepository assetRepository;

    @GetMapping("/trades/{pairId}")
    public RecentTradesResponse recentTrades(@PathVariable Long pairId) {
        final var pair = pairRepository.findByPairIdAndStatus(pairId, Pair.Status.ACTIVE).orElseThrow(PairNotFoundException::new);
        final var base = assetRepository.findByAssetIdAndStatus(pair.getBaseAssetId(), Asset.Status.ACTIVE).orElseThrow(AssetNotFoundException::new);
        final var quote = assetRepository.findByAssetIdAndStatus(pair.getQuoteAssetId(), Asset.Status.ACTIVE).orElseThrow(AssetNotFoundException::new);
        final var trades = matchRepository.findRecentTradesByPairId(pair.getPairId(), 100);
        final var responseTrades = trades.stream().map((trade) -> {
            final var realPrice = trade.getPrice().scaleByPowerOfTen(-1*quote.getScale());
            final var realQty = trade.getQty().scaleByPowerOfTen(-1*base.getScale());
            final var realTotal = trade.getTotal().scaleByPowerOfTen(-1*quote.getScale());
            return RecentTradesResponse.RecentTrade.builder()
                    .tradeId(trade.getMatchId())
                    .price(realPrice.stripTrailingZeros().toPlainString())
                    .qty(realQty.stripTrailingZeros().toPlainString())
                    .total(realTotal.stripTrailingZeros().toPlainString())
                    .utc(trade.getUtc())
                    .build();
        }).collect(Collectors.toList());
        return RecentTradesResponse.builder()
                .trades(responseTrades)
                .build();
    }

}
