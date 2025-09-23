package co.com.pragma.usecase.capacidad;

import co.com.pragma.model.capacidad.CalcularCapacidadRequest;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.gateway.CapacidadPublisher;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CalcularCapacidadUseCase {

    private final SolicitudRepository solicitudRepository;
    private final CapacidadPublisher capacidadPublisher;

    public Mono<Void> solicitarValidacion(Long solicitudId) {
        return solicitudRepository.findById(solicitudId)
                .switchIfEmpty(Mono.error(new BusinessException("Solicitud no existe")))
                .flatMap(sol ->
                        solicitudRepository.deudaMensualAprobadas(sol.getEmail())
                                .defaultIfEmpty(BigDecimal.ZERO)
                                .flatMap(deuda -> {
                                    var req = CalcularCapacidadRequest.builder()
                                            .idSolicitud(sol.getId())
                                            .email(sol.getEmail())
                                            .monto(sol.getMonto())
                                            .plazo(sol.getPlazo())
                                            .tasaInteres(null)
                                            .deudaMensualActual(deuda)
                                            .ingresosTotales(null) // ponlo si lo manejas
                                            .build();
                                    return capacidadPublisher.publicar(req);
                                })
                );
        }
    }
