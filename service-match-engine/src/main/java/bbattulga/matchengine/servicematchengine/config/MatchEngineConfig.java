package bbattulga.matchengine.servicematchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

@Configuration
@ConfigurationProperties(prefix = "match-engine")
@Getter
@Setter
public class MatchEngineConfig {
    private String base;
    private String quote;
    private BigInteger makerFee;
    private BigInteger takerFee;
    private Integer baseScale;
    private Long baseTick;
    private Integer quoteScale;
    private Long quoteTick;
}
