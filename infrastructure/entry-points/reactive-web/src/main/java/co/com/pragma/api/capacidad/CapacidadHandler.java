package co.com.pragma.api.capacidad;

import co.com.pragma.usecase.capacidad.CalcularCapacidadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CapacidadHandler {

    private final CalcularCapacidadUseCase useCase;

    public Mono<ServerResponse> disparar(ServerRequest req) {
        Long id = Long.valueOf(req.pathVariable("idSolicitud"));
        return useCase.solicitarValidacion(id)
                .then(ServerResponse.status(HttpStatus.ACCEPTED).build());
    }
}
