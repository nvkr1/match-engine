package bbattulga.matchengine.servicematchengine.config;

import bbattulga.matchengine.libmodel.engine.http.client.MatchEngineClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class MatchEngineClientConfig {

    private final MatchEngineConfig config;

    @Bean
    MatchEngineClient matchEngineClient() {
        final var webClient = WebClient.builder()
                .baseUrl(config.getMatchEngineUrl())
                .build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .build();
        return httpServiceProxyFactory.createClient(MatchEngineClient.class);
    }

}
