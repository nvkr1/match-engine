package bbattulga.matchengine.servicematchengine.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {
    @Builder.Default
    private String status = "SUCCESS";
}
