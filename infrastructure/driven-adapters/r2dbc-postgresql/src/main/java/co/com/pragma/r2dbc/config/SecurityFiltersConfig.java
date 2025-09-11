package co.com.pragma.r2dbc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.Objects;

@Configuration
public class SecurityFiltersConfig {

    @Bean
    public ExchangeFilterFunction bearerPropagator() {
        return (request, next) ->
                ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication)
                        .filter(Objects::nonNull)
                        .map(auth -> String.valueOf(auth.getCredentials()))  // el JWT
                        .flatMap(token -> {
                            ClientRequest newReq = ClientRequest.from(request)
                                    .headers(h -> h.setBearerAuth(token))
                                    .build();
                            return next.exchange(newReq);
                        })
                        .switchIfEmpty(next.exchange(request));
    }
}

