package co.com.pragma.api.capacidad;

import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.usecase.capacidad.CalcularCapacidadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CapacidadHandler {

    private final CalcularCapacidadUseCase useCase;

    public Mono<ServerResponse> disparar(ServerRequest req) {
        // 1) validar id numérico
        final String raw = req.pathVariable("idSolicitud");
        final long id;
        try {
            id = Long.parseLong(raw);
            if (id <= 0) throw new NumberFormatException("non-positive");
        } catch (NumberFormatException e) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("error", "idSolicitud inválido"));
        }

        // 2) ejecutar UC y responder 202 con cuerpo útil
        return useCase.solicitarValidacion(id)
                .then(ServerResponse.accepted()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "status", "EN_COLA",
                                "idSolicitud", id)))
                // 3) si no usas un filtro global para exceptions, trata aquí las de negocio
                .onErrorResume(BusinessException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", ex.getMessage())));
    }
}
