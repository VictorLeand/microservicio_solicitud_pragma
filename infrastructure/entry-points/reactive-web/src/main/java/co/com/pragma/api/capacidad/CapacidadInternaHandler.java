package co.com.pragma.api.capacidad;

import co.com.pragma.model.capacidad.CalcularCapacidadDecision;
import co.com.pragma.model.estadoprestamo.gateways.EstadoPrestamoRepository;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CapacidadInternaHandler {

    private final SolicitudRepository solicitudRepository;
    private final EstadoPrestamoRepository estadoRepo;

    // GET /internal/solicitudes/deuda-mensual?email=...
    public Mono<ServerResponse> deudaMensual(ServerRequest req) {
        String email = req.queryParam("email").orElse(null);
        if (email == null || email.isBlank()) {
            return Mono.error(new BusinessException("email requerido"));
        }
        // Reusa el SQL que ya tienes para deuda mensual de aprobadas
        return solicitudRepository.deudaMensualAprobadas(email)
                .defaultIfEmpty(BigDecimal.ZERO)
                .flatMap(total -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(java.util.Map.of("email", email, "deudaMensual", total)));
    }

    // POST /internal/solicitudes/{id}/decision
    public Mono<ServerResponse> callback(ServerRequest req) {
        Long idSolicitud = Long.valueOf(req.pathVariable("id"));
        return req.bodyToMono(CalcularCapacidadDecision.class)
                .flatMap(dec -> {
                    String estado = switch (dec.getDecision()) {
                        case "APROBADO" -> "ACEPTADA";
                        case "RECHAZADO" -> "RECHAZADA";
                        case "REVISION MANUAL" -> "REVISIÓN MANUAL";
                        default -> "REVISIÓN MANUAL";
                    };
                    return estadoRepo.findIdByNombre(estado)
                            .flatMap(idEstado -> solicitudRepository.updateEstadoById(idSolicitud, idEstado))
                            .then(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(java.util.Map.of("ok", true)));
                });
    }
}
