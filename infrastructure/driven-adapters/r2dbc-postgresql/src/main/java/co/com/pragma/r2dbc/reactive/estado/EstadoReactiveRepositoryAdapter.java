package co.com.pragma.r2dbc.reactive.estado;

import co.com.pragma.model.estadoprestamo.EstadoPrestamo;
import co.com.pragma.model.estadoprestamo.gateways.EstadoPrestamoRepository;
import co.com.pragma.model.transactional.TransactionalService;
import co.com.pragma.r2dbc.entity.EstadoPrestamoEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class EstadoReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        EstadoPrestamo/* change for domain model */,
        EstadoPrestamoEntity/* change for adapter model */,
        Long,
        EstadoReactiveRepository
> implements EstadoPrestamoRepository {

    private final TransactionalService transactionalService;

    public EstadoReactiveRepositoryAdapter(EstadoReactiveRepository repository, ObjectMapper mapper, TransactionalService transactionalService) {
        super(repository, mapper, entity -> mapper.map(entity, EstadoPrestamo.class/* change for domain model */));
        this.transactionalService = transactionalService;
    }

    @Override
    public Mono<EstadoPrestamo> save(EstadoPrestamo estadoPrestamo) {
        return Mono.just(estadoPrestamo)
                .doOnNext(estado -> log.info("Iniciando guardado de estado : {}" , estado))
                .flatMap(super::save)
                .as(transactionalService::transactional)
                .doOnNext(estado -> log.info("Estado guardada : {}" , estado));
    }

    @Override
    public Mono<EstadoPrestamo> findById(Long id){
        return Mono.just(id)
                .doOnNext(ids -> log.info("Iniciando búsqueda de estado : {}" , ids))
                .flatMap(super::findById)
                .as(transactionalService::transactional)
                .doOnNext(ids -> log.info("Estado recibido : {}" , ids));
    }

    @Override
    public Mono<Long> findIdByNombre(String nombre) {
        return repository.findIdByNombre(nombre);
    }

}
