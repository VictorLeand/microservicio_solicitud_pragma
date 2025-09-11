package co.com.pragma.usecase.tipoprestamo;

import co.com.pragma.model.estadoprestamo.EstadoPrestamo;
import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TipoPrestamoUseCase {

    private final TipoPrestamoRepository tipoPrestamoRepository;

    public TipoPrestamoUseCase(TipoPrestamoRepository tipoPrestamoRepository) {
        this.tipoPrestamoRepository = tipoPrestamoRepository;
    }


    public Mono<TipoPrestamo> saveTipo(TipoPrestamo tipoPrestamo) {
        return Mono.just(tipoPrestamo)
                .flatMap(tipoPrestamoRepository::save);
    }

}
