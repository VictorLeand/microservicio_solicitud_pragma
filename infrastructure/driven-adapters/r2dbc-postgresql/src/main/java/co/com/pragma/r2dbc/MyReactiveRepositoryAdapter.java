package co.com.pragma.r2dbc;

import co.com.pragma.model.user.Solicitud;
import co.com.pragma.model.user.gateways.SolicitudRepository;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
        Long,
        MyReactiveRepository

> implements SolicitudRepository {

    private final TransactionalOperator transactionalOperator;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper,
                                       TransactionalOperator transactionalOperator) {
        super(repository, mapper, entity -> mapper.map(entity, Solicitud.class));

        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        return Mono.just(solicitud)
                .doOnNext(solicitudes -> log.info("Iniciando guardado de solicitud : {}" , solicitudes))
                .flatMap(super::save)
                .as(transactionalOperator::transactional)
                .doOnNext(solicitudes -> log.info("Solicitud guardada : {}" , solicitudes));
    }
}
