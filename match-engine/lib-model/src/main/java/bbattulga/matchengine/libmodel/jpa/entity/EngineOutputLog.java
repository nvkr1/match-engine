package bbattulga.matchengine.libmodel.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ex_output_log")
@Getter
@Setter
public class EngineOutputLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID engineOutputLogId;
    private String type;
    @Column(length = 2048)
    private String payload;
    private Long utc;
    private LocalDateTime createdAt;
}
