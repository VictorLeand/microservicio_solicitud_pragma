package co.com.pragma.r2dbc.reactive.tipo;

import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.model.transactional.TransactionalService;
import co.com.pragma.r2dbc.entity.TipoPrestamoEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class TipoReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo/* change for domain model */,
        TipoPrestamoEntity/* change for adapter model */,
        Long,
        TipoReactiveRepository
> implements TipoPrestamoRepository {

            private final TransactionalService transactionalService;

    public TipoReactiveRepositoryAdapter(TipoReactiveRepository repository, ObjectMapper mapper,
                                         TransactionalService transactionalService) {

        super(repository, mapper, entity -> mapper.map(entity, TipoPrestamo.class/* change for domain model */));
        this.transactionalService = transactionalService;
    }

    @Override
    public Mono<TipoPrestamo> save(TipoPrestamo tipoPrestamo) {
        return Mono.just(tipoPrestamo)
                .doOnNext(tipo -> log.info("Iniciando guardado de tipo de prestamo : {}" , tipo))
                .flatMap(super::save)
                .as(transactionalService::transactional)
                .doOnNext(tipo -> log.info("Tipo de prestamo guardada : {}" , tipo));
    }

    @Override
    public Mono<TipoPrestamo> findById(Long id) {
        return Mono.just(id)
                .doOnNext(tipo -> log.info("Iniciando búsqueda de tipo de prestamo : {}" , tipo))
                .flatMap(super::findById)
                .as(transactionalService::transactional)
                .doOnNext(tipo -> log.info("Tipo de prestamo recibido : {}" , tipo));
    }



}
