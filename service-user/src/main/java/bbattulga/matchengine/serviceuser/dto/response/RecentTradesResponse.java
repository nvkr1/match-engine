package bbattulga.matchengine.serviceuser.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RecentTradesResponse {

    List<RecentTrade> trades;

    @Getter
    @Setter
    @Builder
    public static final class RecentTrade {
        private String tradeId;
        private String price;
        private String qty;
        private String total;
        private Long utc;
    }
}
