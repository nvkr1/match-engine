package bbattulga.matchengine.servicesimulation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("service-simulation")
@Getter
@Setter
public class SimulationConfig {
    private String matchEngineUrl;
    private Long userCount;
    private Long orderCount;
    private Long pairId;
    private Long startPrice;
    private Long endPrice;
    private Long priceVariance;
    private Long startQty;
    private Long endQty;
    private Long qtyVariance;
}
