package co.com.pragma.consumer.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class RestConsumerConfig {

    @Value("${adapter.http.timeout:5000}")
    private int timeout;

    @Bean
    @Qualifier("restConsumerClient")
    public WebClient restConsumerClient(WebClient.Builder builder,
                                        ExchangeFilterFunction bearerPropagator,
                                        @Value("${adapter.userpaginable.url}") String baseUrl) {
        return builder.baseUrl(baseUrl).filter(bearerPropagator).build();
    }



    @Bean
    @Qualifier("usuariosWebClient")
    public WebClient usuariosWebClient(WebClient.Builder builder,
                                       ExchangeFilterFunction bearerPropagator,
                                       @Value("${adapter.restconsumer.url}") String baseUrl,
                                       @Value("${adapter.http.timeout}") long timeoutMs) {
        return builder
                .baseUrl(baseUrl)
                .filter(bearerPropagator) // ← propaga Authorization desde el SecurityContext
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(java.time.Duration.ofMillis(timeoutMs))
                ))
                .build();
    }


    private ClientHttpConnector getClientHttpConnector() {
        /*
        IF YO REQUIRE APPEND SSL CERTIFICATE SELF SIGNED: this should be in the default cacerts trustore
        */
//        SslContext sslContext = null;
//        try {
//            sslContext = SslContextBuilder.forClient(). trustManager(InsecureTrustManagerFactory. INSTANCE). build();
//        } catch (SSLException e) {
//            throw new RuntimeException(e);
//
//        }
//            SslContext finalSslContext = sslContext;


        return new ReactorClientHttpConnector(
                HttpClient.create()
                        // .secure(ssl -> ssl.sslContext(finalSslContext))
                        .compress(true)
                        .keepAlive(true)
                        .option(CONNECT_TIMEOUT_MILLIS, timeout)
                        .doOnConnected(conn -> {
                            conn.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                            conn.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                        })
        );
    }
}
