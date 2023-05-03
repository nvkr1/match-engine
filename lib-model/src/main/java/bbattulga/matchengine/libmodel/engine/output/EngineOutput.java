package bbattulga.matchengine.libmodel.engine.output;

import bbattulga.matchengine.libmodel.consts.OutputType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EngineOutput {
    private OutputType type;
    private String payload;
}
