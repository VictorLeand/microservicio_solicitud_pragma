package co.com.pragma.usecase.estadoprestamo;

import co.com.pragma.model.estadoprestamo.EstadoPrestamo;
import co.com.pragma.model.estadoprestamo.gateways.EstadoPrestamoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EstadoPrestamoUseCase {

    private final EstadoPrestamoRepository estadoPrestamoRepository;


    public EstadoPrestamoUseCase(EstadoPrestamoRepository estadoPrestamoRepository) {
        this.estadoPrestamoRepository = estadoPrestamoRepository;
    }

    public Mono<EstadoPrestamo> saveEstado(EstadoPrestamo estadoPrestamo) {
        return Mono.justOrEmpty(estadoPrestamo)
                .flatMap(estadoPrestamoRepository::save);
    }

    public Flux<EstadoPrestamo> findById(Long id){
        return Flux.just(id)
                .flatMap(estadoPrestamoRepository::findById);
    }

}
