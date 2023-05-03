package bbattulga.matchengine.serviceuser.dto.request;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private String uid; // TODO:: Authentication
    private OrderSide side;
    private Long pairId;
    private Double price;
    private Double qty;
    private Double total;
}
