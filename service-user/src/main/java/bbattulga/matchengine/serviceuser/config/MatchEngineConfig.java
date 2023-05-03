package bbattulga.matchengine.serviceuser.config;

import bbattulga.matchengine.serviceuser.service.MatchEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class MatchEngineConfig {

    private final ServiceUserConfig config;

    @Bean
    MatchEngineService matchEngineClient() {
        final var webClient = WebClient.builder()
                .baseUrl(config.getMatchEngineUrl())
                .build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .build();
        return httpServiceProxyFactory.createClient(MatchEngineService.class);
    }

}
