package co.com.pragma.r2dbc.reactive.estado;

import co.com.pragma.r2dbc.entity.EstadoPrestamoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface EstadoReactiveRepository extends ReactiveCrudRepository<EstadoPrestamoEntity, Long>, ReactiveQueryByExampleExecutor<EstadoPrestamoEntity> {


    @Query("""
        SELECT id_estado
        FROM public.estados
        WHERE UPPER(nombre) = UPPER(:nombre)
        LIMIT 1
    """)
    Mono<Long> findIdByNombre(String nombre);
}
