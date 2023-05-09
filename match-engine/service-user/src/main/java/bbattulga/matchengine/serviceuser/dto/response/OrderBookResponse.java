package bbattulga.matchengine.serviceuser.dto.response;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderBookResponse {

    @Builder
    @Getter
    @Setter
    public static final class Depth {
        private OrderSide side;
        private String price;
        private String qty;
        private String total;
    }
    List<Depth> ask;
    List<Depth> bid;
}
