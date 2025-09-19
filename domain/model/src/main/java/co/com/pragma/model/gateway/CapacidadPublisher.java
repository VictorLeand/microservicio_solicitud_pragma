package co.com.pragma.model.gateway;

import co.com.pragma.model.capacidad.CalcularCapacidadRequest;
import reactor.core.publisher.Mono;

public interface CapacidadPublisher {

    Mono<Void> publicar(CalcularCapacidadRequest request);
}
