package co.com.pragma.api.rest.solicitud;

import co.com.pragma.api.dto.ActualizarEstadoRequest;
import co.com.pragma.model.estadoprestamo.gateways.EstadoPrestamoRepository;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.gateway.NotificacionPublisher;
import co.com.pragma.model.lambdas.NotificacionMensaje;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SolicitudEstadoHandler {

    private final SolicitudRepository solicitudRepository;
    private final EstadoPrestamoRepository estadoRepo;
    private final NotificacionPublisher notificacionPublisher;
    private final ObjectMapper objectMapper; // Inyecta el ObjectMapper de Spring

    public Mono<ServerResponse> actualizar(ServerRequest req) {
        Long idSolicitud = Long.valueOf(req.pathVariable("id"));

        return req.bodyToMono(ActualizarEstadoRequest.class)
                .switchIfEmpty(Mono.error(new BusinessException("Body requerido")))
                .flatMap(body -> {
                    String estado = body.getEstado();
                    if (estado == null || estado.isBlank()) {
                        return Mono.error(new BusinessException("estado requerido"));
                    }
                    String estadoNormalizado = estado.trim().toUpperCase();

                    return estadoRepo.findIdByNombre(estadoNormalizado)
                            .switchIfEmpty(Mono.error(new BusinessException("Estado no válido: " + estado)))
                            .flatMap(idEstado -> solicitudRepository.updateEstadoById(idSolicitud, idEstado))
                            .flatMap(rows -> {
                                if (rows == 0) return Mono.error(new BusinessException("Solicitud no encontrada: " + idSolicitud));
                                return solicitudRepository.findById(idSolicitud);
                            })
                            .flatMap(sol -> {
                                NotificacionMensaje msg = NotificacionMensaje.builder()
                                        .email(sol.getEmail())
                                        .estado(estadoNormalizado)
                                        .asunto("Notificación de estado de prestamo") // opcional; la Lambda puede fijarlo
                                        .build();

                                return notificacionPublisher.publicar(msg).thenReturn(sol);
                            });
                })
                .flatMap(sol -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("estado", "ACTUALIZADO", "id", sol.getId(), "email", sol.getEmail())));
    }
}
