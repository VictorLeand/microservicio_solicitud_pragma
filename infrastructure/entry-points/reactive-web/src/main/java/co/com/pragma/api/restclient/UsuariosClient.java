package co.com.pragma.api.restclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UsuariosClient {

    private final WebClient webClient;

    public UsuariosClient(@Qualifier("usuariosWebClient") WebClient webClient) {
        this.webClient = webClient;
    } // 👈 desambiguar

    public Mono<Boolean> validarUsuario(String email, String documento) {
        return webClient.get()
                .uri("/api/v1/validaruser/email/{email}/document/{doc}", email, documento)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
