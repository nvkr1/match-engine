package bbattulga.matchengine.servicematchengine;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.engine.http.request.CancelOrderRequest;
import bbattulga.matchengine.libmodel.engine.http.request.LimitOrderRequest;
import bbattulga.matchengine.libmodel.exception.MatchNotFoundException;
import bbattulga.matchengine.libmodel.exception.OrderNotFoundException;
import bbattulga.matchengine.libmodel.jpa.repository.EngineOutputLogRepository;
import bbattulga.matchengine.libmodel.jpa.repository.MatchRepository;
import bbattulga.matchengine.libmodel.jpa.repository.OrderLogRepository;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import bbattulga.matchengine.servicematchengine.config.MatchEngineConfig;
import bbattulga.matchengine.servicematchengine.config.RabbitMQConfig;
import bbattulga.matchengine.servicematchengine.service.place.CancelOrderPlaceService;
import bbattulga.matchengine.servicematchengine.service.place.LimitOrderPlaceService;
import bbattulga.matchengine.servicematchengine.service.snapshot.OrderBookSnapshotService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ServiceMatchEngineApplicationTests {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private EngineOutputLogRepository engineOutputLogRepository;

	@Autowired
	private OrderLogRepository orderLogRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OrderBookSnapshotService orderBookSnapshotService;

	@Autowired
	private LimitOrderPlaceService limitOrderPlaceService;

	@Autowired
	private CancelOrderPlaceService cancelOrderPlaceService;

	@Autowired
	private RabbitAdmin rabbitAdmin;

	@Autowired
	private MatchEngineConfig config;

	void waitForQueueFinish() throws InterruptedException {
		Thread.sleep(1200);
		boolean isQueueEmpty = false;
		System.out.println("Waiting For " + RabbitMQConfig.QUEUE_ENGINE_OUT + " queue to be consumed");
		while (!isQueueEmpty) {
			final var engineOutQ = rabbitAdmin.getQueueInfo(RabbitMQConfig.QUEUE_ENGINE_OUT);
			if (engineOutQ == null) {
				System.out.println(RabbitMQConfig.QUEUE_ENGINE_OUT + " Queue not found");
				continue;
			}
			if (engineOutQ.getMessageCount() == 0) {
				isQueueEmpty = true;
				System.out.println("Expects " + RabbitMQConfig.QUEUE_ENGINE_OUT + " queue is empty");
			}
		}
	}

	@BeforeEach
	void beforeEach() throws Exception {
		clearPreviousData();
	}

	void clearPreviousData() throws Exception {
		System.out.println("Clear Previous Data");
		waitForQueueFinish();
		final var prevOrders = orderRepository.findByStatusInOrderByUtcAsc(List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED));
		prevOrders.forEach((order) -> {
			if (order.getStatus().equals(OrderStatus.OPEN))
			cancelOrderPlaceService.placeCancelOrder(CancelOrderRequest.builder()
							.id(order.getOrderId().toString())
							.side(order.getSide())
							.price(order.getPrice())
					.build());
		});
		waitForQueueFinish();
		System.out.println("Deleting previous data");
		orderRepository.deleteAll();
		orderLogRepository.deleteAll();
		matchRepository.deleteAll();
		engineOutputLogRepository.deleteAll();
		System.out.println("Deleted previous data");
	}

	// all ask price > buy price
	@Test
	@Order(1)
	void checkOrderBook() throws JsonProcessingException, InterruptedException {
		System.out.println("Checking Order Book Snapshot");
		final var numOrders = 500;
		final var startPrice = BigInteger.valueOf(20_000).multiply(BigInteger.valueOf(config.getQuoteTick()));
		final var qty = BigInteger.valueOf(config.getBaseTick());
		final var realQty = qty.divide(BigInteger.valueOf(config.getBaseTick()));
		final var endPrice = startPrice.add(BigInteger.valueOf(numOrders));
		OrderSide orderSide = OrderSide.BUY;
		for (var price = startPrice; price.compareTo(endPrice) <= 0; price = price.add(BigInteger.ONE)) {
			limitOrderPlaceService.placeLimitOrder(LimitOrderRequest.builder()
					.id(UUID.randomUUID().toString())
					.uid(UUID.randomUUID().toString())
					.side(orderSide)
					.price(price)
					.qty(qty)
					.total(price.multiply(realQty))
					.utc(Instant.now().toEpochMilli())
					.build());
			orderSide = orderSide.equals(OrderSide.BUY) ? OrderSide.SELL : OrderSide.BUY;
		}
		// wait for orderbook to initialize asynchronously
		waitForQueueFinish();
		final var resp = orderBookSnapshotService.getSnapshot();
		System.out.println("Order Book Snapshot:");
		objectMapper.writeValueAsString(resp);
		System.out.println("Checking bid price levels");
		final BigInteger[] askCheckLevel = {startPrice.add(BigInteger.ONE)};
		Objects.requireNonNull(resp).getAsks().forEach((level) -> {
			level.getOrders().forEach((o) -> {
				assertThat(level.getPrice()).isEqualTo(askCheckLevel[0]);
				assertThat(level.getPrice()).isEqualTo(o.getPrice());
				assertThat(o.getQty()).isEqualTo(qty);
				assertThat(o.getSide()).isEqualTo(OrderSide.SELL);
				askCheckLevel[0] = askCheckLevel[0].add(BigInteger.ONE);
			});
		});
		final BigInteger[] bidCheckLevel = {startPrice};
		Objects.requireNonNull(resp).getBids().forEach((level) -> {
			level.getOrders().forEach((o) -> {
				assertThat(level.getPrice()).isEqualTo(bidCheckLevel[0]);
				assertThat(level.getPrice()).isEqualTo(o.getPrice());
				assertThat(o.getQty()).isEqualTo(qty);
				assertThat(o.getSide()).isEqualTo(OrderSide.BUY);
				bidCheckLevel[0] = bidCheckLevel[0].add(BigInteger.ONE);
			});
		});
	}

	@Test
	@Order(2)
	void execSellMatch() throws InterruptedException {
		final var buyPrice = BigInteger.valueOf(24000).multiply(BigInteger.valueOf(config.getQuoteTick()));
		final var matchingQty = BigInteger.valueOf(config.getBaseTick());
		final var realQty = new BigDecimal(matchingQty).divide(BigDecimal.valueOf(config.getBaseTick()), RoundingMode.DOWN);
		final var buyTotal = new BigDecimal(buyPrice).multiply(realQty).toBigIntegerExact();
		final var buyId = UUID.randomUUID();
		final var buyUid = UUID.randomUUID();
		final var buy = LimitOrderRequest.builder()
				.id(buyId.toString())
				.uid(buyUid.toString())
				.price(buyPrice)
				.qty(matchingQty)
				.total(buyTotal)
				.side(OrderSide.BUY)
				.utc(Instant.now().toEpochMilli())
				.build();
		limitOrderPlaceService.placeLimitOrder(buy);

		final var sellId = UUID.randomUUID();
		final var sellUid = UUID.randomUUID();
		final var sellPrice = BigInteger.valueOf(23_000).multiply(BigInteger.valueOf(config.getQuoteTick()));
		final var sellTotal = new BigDecimal(sellPrice).multiply(realQty).toBigIntegerExact();
		final var sell = LimitOrderRequest.builder()
				.id(sellId.toString())
				.uid(sellUid.toString())
				.price(sellPrice)
				.qty(matchingQty)
				.total(sellTotal)
				.side(OrderSide.SELL)
				.utc(Instant.now().toEpochMilli())
				.build();
		limitOrderPlaceService.placeLimitOrder(sell);
		waitForQueueFinish();

		final var pBuy = orderRepository.findByOrderCode(buyId).orElseThrow(OrderNotFoundException::new);
		final var pSell = orderRepository.findByOrderCode(sellId).orElseThrow(OrderNotFoundException::new);

		assertThat(pBuy.getUid()).isEqualTo(buyUid);
		assertThat(pBuy.getPrice().compareTo(buy.getPrice())).isEqualTo(0);
		assertThat(pBuy.getQty().compareTo(buy.getQty())).isEqualTo(0);
		assertThat(pBuy.getTotal().compareTo(buy.getTotal())).isEqualTo(0);
		assertThat(pBuy.getUtc()).isEqualTo(buy.getUtc());
		assertThat(pBuy.getFillQty().compareTo(matchingQty)).isEqualTo(0);
		assertThat(pBuy.getFillTotal().compareTo(sellTotal)).isEqualTo(0);
		assertThat(pBuy.getExecQty().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pBuy.getExecTotal().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pBuy.getRemainingQty().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pBuy.getRemainingTotal().compareTo(BigInteger.ZERO)).isEqualTo(0);

		assertThat(pSell.getUid()).isEqualTo(sellUid);
		assertThat(pSell.getPrice().compareTo(sell.getPrice())).isEqualTo(0);
		assertThat(pSell.getQty().compareTo(sell.getQty())).isEqualTo(0);
		assertThat(pSell.getTotal().compareTo(sell.getTotal())).isEqualTo(0);
		assertThat(pSell.getUtc()).isEqualTo(sell.getUtc());
		assertThat(pSell.getFillQty().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pSell.getFillTotal().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pSell.getExecQty().compareTo(matchingQty)).isEqualTo(0);
		assertThat(pSell.getExecTotal().compareTo(sellTotal)).isEqualTo(0);
		assertThat(pSell.getRemainingQty()).isEqualTo(BigInteger.ZERO);
		assertThat(pSell.getRemainingTotal()).isEqualTo(BigInteger.ZERO);

		final var match = matchRepository.findByExecOrderIdAndRemainingOrderId(pSell.getOrderId(), pBuy.getOrderId()).orElseThrow(MatchNotFoundException::new);
		assertThat(match.getQty().compareTo(matchingQty)).isEqualTo(0);
		assertThat(match.getPrice().compareTo(pSell.getPrice())).isEqualTo(0);
		assertThat(match.getTotal().compareTo(sellTotal)).isEqualTo(0);
	}

	@Test
	@Order(3)
	void execBuyMatch() throws InterruptedException {

		final var matchingQty = BigInteger.valueOf(config.getBaseTick());
		final var realQty = new BigDecimal(matchingQty).divide(BigDecimal.valueOf(config.getBaseTick()), RoundingMode.DOWN);

		final var sellId = UUID.randomUUID();
		final var sellUid = UUID.randomUUID();
		final var sellPrice = BigInteger.valueOf(23_000).multiply(BigInteger.valueOf(config.getQuoteTick()));
		final var sellTotal = new BigDecimal(sellPrice).multiply(realQty).toBigIntegerExact();
		final var sell = LimitOrderRequest.builder()
				.id(sellId.toString())
				.uid(sellUid.toString())
				.price(sellPrice)
				.qty(matchingQty)
				.total(sellTotal)
				.side(OrderSide.SELL)
				.utc(Instant.now().toEpochMilli())
				.build();
		limitOrderPlaceService.placeLimitOrder(sell);
		final var buyPrice = BigInteger.valueOf(24000).multiply(BigInteger.valueOf(config.getQuoteTick()));
		final var buyTotal = new BigDecimal(buyPrice).multiply(realQty).toBigIntegerExact();
		final var buyId = UUID.randomUUID();
		final var buyUid = UUID.randomUUID();
		final var buy = LimitOrderRequest.builder()
				.id(buyId.toString())
				.uid(buyUid.toString())
				.price(buyPrice)
				.qty(matchingQty)
				.total(buyTotal)
				.side(OrderSide.BUY)
				.utc(Instant.now().toEpochMilli())
				.build();
		limitOrderPlaceService.placeLimitOrder(buy);

		waitForQueueFinish();

		final var pBuy = orderRepository.findByOrderCode(buyId).orElseThrow(OrderNotFoundException::new);
		final var pSell = orderRepository.findByOrderCode(sellId).orElseThrow(OrderNotFoundException::new);

		assertThat(pBuy.getUid()).isEqualTo(buyUid);
		assertThat(pBuy.getPrice().compareTo(buy.getPrice())).isEqualTo(0);
		assertThat(pBuy.getQty().compareTo(buy.getQty())).isEqualTo(0);
		assertThat(pBuy.getTotal().compareTo(buy.getTotal())).isEqualTo(0);
		assertThat(pBuy.getUtc()).isEqualTo(buy.getUtc());
		assertThat(pBuy.getFillQty().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pBuy.getFillTotal().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pBuy.getExecQty().compareTo(matchingQty)).isEqualTo(0);
		assertThat(pBuy.getExecTotal().compareTo(pSell.getTotal())).isEqualTo(0);
		assertThat(pBuy.getRemainingQty().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pBuy.getRemainingTotal().compareTo(BigInteger.ZERO)).isEqualTo(0);

		assertThat(pSell.getUid()).isEqualTo(sellUid);
		assertThat(pSell.getPrice().compareTo(sell.getPrice())).isEqualTo(0);
		assertThat(pSell.getQty().compareTo(sell.getQty())).isEqualTo(0);
		assertThat(pSell.getTotal().compareTo(sell.getTotal())).isEqualTo(0);
		assertThat(pSell.getUtc()).isEqualTo(sell.getUtc());
		assertThat(pSell.getFillQty().compareTo(matchingQty)).isEqualTo(0);
		assertThat(pSell.getFillTotal().compareTo(pSell.getTotal())).isEqualTo(0);
		assertThat(pSell.getExecQty().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pSell.getExecTotal().compareTo(BigInteger.ZERO)).isEqualTo(0);
		assertThat(pSell.getRemainingQty()).isEqualTo(BigInteger.ZERO);
		assertThat(pSell.getRemainingTotal()).isEqualTo(BigInteger.ZERO);

		final var match = matchRepository.findByExecOrderIdAndRemainingOrderId(pBuy.getOrderId(), pSell.getOrderId()).orElseThrow(MatchNotFoundException::new);
		assertThat(match.getQty().compareTo(matchingQty)).isEqualTo(0);
		assertThat(match.getPrice().compareTo(pSell.getPrice())).isEqualTo(0);
		assertThat(match.getTotal().compareTo(sellTotal)).isEqualTo(0);
	}

}
