package bbattulga.matchengine.libmodel.jpa.entity;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ex_order_log")
@Getter
@Setter
public class OrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderLogId;
    private UUID orderId;
    private UUID uid;
    @Enumerated(EnumType.STRING)
    private OrderSide side;
    private Long pairId;
    private BigInteger qty;
    private BigInteger price;
    private BigInteger total;
    private BigInteger remainingQty;
    private BigInteger remainingTotal;
    private BigInteger execQty = BigInteger.ZERO;
    private BigInteger execTotal = BigInteger.ZERO;
    private BigInteger fillQty = BigInteger.ZERO;
    private BigInteger fillTotal = BigInteger.ZERO;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private Long utc;
    private Long ns;
    private LocalDateTime createdAt;
}
