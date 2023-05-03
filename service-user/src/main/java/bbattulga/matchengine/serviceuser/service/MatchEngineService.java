package bbattulga.matchengine.serviceuser.service;

import bbattulga.matchengine.libmodel.engine.http.request.CancelOrderRequest;
import bbattulga.matchengine.libmodel.engine.http.request.LimitOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface MatchEngineService {

    @PostExchange("/{pairSymbol}/order/limit")
    ResponseEntity<Void> limitOrder(@PathVariable String pairSymbol, @RequestBody LimitOrderRequest request);

    @PostExchange("/order/cancel")
    ResponseEntity<Void> cancelOrder(@PathVariable String pairSymbol, @RequestBody CancelOrderRequest request);
}
