package bbattulga.matchengine.libmodel.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ex_output")
@Getter
@Setter
public class EngineOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID engineOutputId;
    private String type;
    private String payload;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
