package bbattulga.matchengine.libmodel.engine.http.client;

import bbattulga.matchengine.libmodel.engine.OrderBookSnapshotData;
import bbattulga.matchengine.libmodel.engine.http.request.CancelOrderRequest;
import bbattulga.matchengine.libmodel.engine.http.request.LimitOrderRequest;
import bbattulga.matchengine.libmodel.engine.http.response.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface MatchEngineClient {

    @PostExchange("/{symbol}/order/limit")
    ResponseEntity<OrderResponse> limitOrder(@PathVariable(name = "symbol") String symbol, @RequestBody LimitOrderRequest request);

    @PostExchange("/{symbol}/order/cancel")
    ResponseEntity<OrderResponse> cancelOrder(@PathVariable(name = "symbol") String symbol, @RequestBody CancelOrderRequest request);

    @GetExchange("/{symbol}/snapshot/order-book")
    ResponseEntity<OrderBookSnapshotData> orderBookSnapshot(@PathVariable(name = "symbol") String symbol);

}
