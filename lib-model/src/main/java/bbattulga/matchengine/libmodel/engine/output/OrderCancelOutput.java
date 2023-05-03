package bbattulga.matchengine.libmodel.engine.output;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelOutput {
    /**
     * Matching Quantity
     */
    private String orderId;
    private String base;
    private String quote;
}
