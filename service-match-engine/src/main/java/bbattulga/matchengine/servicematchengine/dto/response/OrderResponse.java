package bbattulga.matchengine.servicematchengine.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {
    private String status;
    private String message;
}
