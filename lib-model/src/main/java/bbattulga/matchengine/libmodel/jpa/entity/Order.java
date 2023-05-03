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
@Table(name = "ex_order")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;
    private UUID uid;
    @Enumerated(EnumType.STRING)
    private OrderSide side;
    private Long pairId;
    private BigInteger qty;
    private BigInteger price;
    private BigInteger total;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private Long utc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
