package co.com.pragma.model.estadoprestamo.gateways;


import co.com.pragma.model.estadoprestamo.EstadoPrestamo;
import reactor.core.publisher.Mono;

public interface EstadoPrestamoRepository {

    Mono<EstadoPrestamo> save(EstadoPrestamo estadoPrestamo);
    Mono<EstadoPrestamo> findById(Long id);
    Mono<Long> findIdByNombre(String nombre);
}
