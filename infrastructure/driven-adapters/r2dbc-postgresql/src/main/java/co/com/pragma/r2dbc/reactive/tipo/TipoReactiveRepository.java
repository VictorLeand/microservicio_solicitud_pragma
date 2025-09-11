package co.com.pragma.r2dbc.reactive.tipo;

import co.com.pragma.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface TipoReactiveRepository extends ReactiveCrudRepository<TipoPrestamoEntity, Long>, ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {

}
