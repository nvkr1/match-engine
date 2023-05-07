package bbattulga.matchengine.libmodel.engine.http.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    @Builder.Default
    private String status = "SUCCESS";
}
