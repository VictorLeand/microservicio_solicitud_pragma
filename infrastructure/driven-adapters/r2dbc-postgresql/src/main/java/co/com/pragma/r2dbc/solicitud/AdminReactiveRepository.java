package co.com.pragma.r2dbc.solicitud;

import co.com.pragma.model.admin.Admin;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface AdminReactiveRepository extends ReactiveCrudRepository<Admin, Long>, ReactiveQueryByExampleExecutor<Admin> {

}
