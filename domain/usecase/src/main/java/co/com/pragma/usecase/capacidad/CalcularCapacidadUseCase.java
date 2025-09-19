package co.com.pragma.usecase.capacidad;

import co.com.pragma.model.capacidad.CalcularCapacidadRequest;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.gateway.CapacidadPublisher;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CalcularCapacidadUseCase {

    private final SolicitudRepository solicitudRepository;
    private final CapacidadPublisher capacidadPublisher;

    public Mono<Void> solicitarValidacion(Long solicitudId) {
        return solicitudRepository.findById(solicitudId)
                .switchIfEmpty(Mono.error(new BusinessException("Solicitud no existe")))
                .flatMap(sol -> {
                    if (sol.getIdTipoPrestamo() == null) {
                        return Mono.error(new BusinessException("Solicitud sin tipo de préstamo"));
                    }
                    // Construye el request para la Lambda
                    var req = CalcularCapacidadRequest.builder()
                            .idSolicitud(sol.getId())
                            .email(sol.getEmail())
                            .monto(sol.getMonto())
                            .plazo(sol.getPlazo())
                            .tasaInteres(null)
                            .build();
                    return capacidadPublisher.publicar(req);
                });
    }
}
