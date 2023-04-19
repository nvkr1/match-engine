package bbattulga.matchengine.servicematchengine.service;

import bbattulga.matchengine.servicematchengine.dto.engine.OrderBookPriceLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class OrderBookService {
    private final TreeMap<BigInteger, OrderBookPriceLevel> bids = new TreeMap<>();
    private final TreeMap<BigInteger, OrderBookPriceLevel> asks = new TreeMap<>();
}
