package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Mono<Solicitud> save(Solicitud solicitud);
}
