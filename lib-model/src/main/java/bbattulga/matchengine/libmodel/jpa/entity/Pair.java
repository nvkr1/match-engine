package bbattulga.matchengine.libmodel.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "ex_pair")
@Getter
@Setter
public class Pair {

    public enum Status {
        ACTIVE,
        INACTIVE,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pairId;
    private String symbol;
    private Long baseAssetId;
    private Long quoteAssetId;
    private Integer baseDecimal;
    private Integer quoteDecimal;
    private BigInteger lastPrice;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
