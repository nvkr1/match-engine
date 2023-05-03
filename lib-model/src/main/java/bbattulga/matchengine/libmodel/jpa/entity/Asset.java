package bbattulga.matchengine.libmodel.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ex_asset")
@Getter
@Setter
public class Asset {

    public enum Status {
        ACTIVE,
        INACTIVE,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;
    private String symbol;
    private String name;
    private Integer scale;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
