package bbattulga.matchengine.libmodel.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ex_match")
@Getter
@Setter
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID matchId;
    private Long pairId;
    private Long baseAssetId;
    private Long quoteAssetId;
    private UUID execOrderId;
    private UUID remainingOrderId;
    private BigInteger price;
    private BigInteger qty;
    private BigInteger total;
    private BigInteger makerFee;
    private BigInteger takerFee;
    private Long utc;
    private Long ns;
    private LocalDateTime createdAt;
}
