package co.com.pragma.usecase.user;

import co.com.pragma.model.user.Solicitud;
import co.com.pragma.model.user.gateways.SolicitudRepository;
import reactor.core.publisher.Mono;

public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;

    public SolicitudUseCase(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    public Mono<Solicitud> saveSolicitud(Solicitud solicitud) {
        return Mono.just(solicitud)
                .flatMap(solicitudRepository::save);
    }
}
