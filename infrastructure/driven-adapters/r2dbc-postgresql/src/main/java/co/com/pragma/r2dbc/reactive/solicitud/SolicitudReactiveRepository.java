package co.com.pragma.r2dbc.reactive.solicitud;

import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.model.solicitud.gateways.SolicitudResumenRow;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

// TODO: This file is just an example, you should delete or modify it
public interface SolicitudReactiveRepository extends ReactiveCrudRepository<SolicitudEntity, Long>, ReactiveQueryByExampleExecutor<SolicitudEntity> {

    Flux<SolicitudEntity> findByIdEstadoIn(List<Long> estados);

    @Query("""
      SELECT s.id_solicitud  AS "idSolicitud",
             s.monto         AS "monto",
             s.plazo         AS "plazo",
             s.email         AS "email",
             s.id_estado     AS "idEstado",
             ep.nombre       AS "estadoNombre",
             tp.nombre       AS "tipoPrestamo",
             tp.tasa_interes AS "tasaInteres"
      FROM public.solicitudes s
      LEFT JOIN public.tipo_prestamo tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo
      LEFT JOIN public.estados ep      ON ep.id_estado       = s.id_estado
      WHERE ep.nombre IN (:nombres)
      ORDER BY s.id_solicitud
      OFFSET :offset LIMIT :limit
      """)
    Flux<SolicitudResumenRow> findResumenByEstadoNombreIn(
            List<String> nombres, long offset, int limit
    );
}
