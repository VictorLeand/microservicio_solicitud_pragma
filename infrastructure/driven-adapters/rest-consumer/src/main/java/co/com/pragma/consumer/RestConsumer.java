package co.com.pragma.consumer;

import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Service

public class RestConsumer implements UserRepository {
    private final WebClient client;
    private final WebClient clientP;


    public RestConsumer(
            @Qualifier("restConsumerClient") WebClient client,
            @Qualifier("usuariosWebClient") WebClient clientP
    ) {
        this.client = client;
        this.clientP = clientP;
    }


//    @CircuitBreaker(name = "testGet" /*, fallbackMethod = "testGetOk"*/)
    public Mono<Boolean> existsByEmailAndByDocument(String email, String document) {
        String uri = UriComponentsBuilder.fromPath("/api/v1/validaruser")
                .pathSegment("email", email)
                .pathSegment("document", document)
                .build()
                .toUriString();

        log.info("GET validaruser -> {}", uri);

        return clientP.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnNext(x -> log.info("validaruser -> {}", x))
                .onErrorResume(ex -> {
                    log.error("Error consultando validaruser: {}", ex.getMessage(), ex);
                    return Mono.just(false);
                });
    }

    public Mono<User> findByEmailUser(String email) {
        String uri = UriComponentsBuilder.fromPath("/api/v1/getuser")
                .pathSegment("email", email)
                .build()
                .toUriString();

        log.info("GET usuarios -> {}", uri);

        return clientP.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(User.class)
                .doOnNext(x -> log.info("Usuario -> {}", x))
                .onErrorResume(ex -> {
                    log.error("Error consultando usuarios: {}", ex.getMessage(), ex);
                    return Mono.error(new BusinessException("El usuario no existe"));
                });
    }

// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }

//    @CircuitBreaker(name = "testPost")
//    public Mono<ObjectResponse> testPost() {
//        ObjectRequest request = ObjectRequest.builder()
//            .val1("exampleval1")
//            .val2("exampleval2")
//            .build();
//        return client
//                .post()
//                .body(Mono.just(request), ObjectRequest.class)
//                .retrieve()
//                .bodyToMono(ObjectResponse.class);
//    }
}
