package co.com.pragma.r2dbc.reactive.solicitud;

import co.com.pragma.model.PageResponse;
import co.com.pragma.model.admin.Admin;
import co.com.pragma.model.solicitud.AdminFilters;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.transactional.TransactionalService;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.model.solicitud.gateways.SolicitudResumenRow;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class SolicitudReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
        Long,
        SolicitudReactiveRepository

> implements SolicitudRepository {

    private final TransactionalService transactionalService;
    private final SolicitudReactiveRepository repo;
    private final UserRepository userRepository;
    private final DatabaseClient db;


    public SolicitudReactiveRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper,
                                              TransactionalService transactionalService, SolicitudReactiveRepository repo, UserRepository userRepository, DatabaseClient db) {
        super(repository, mapper, entity -> mapper.map(entity, Solicitud.class));
        this.transactionalService = transactionalService;
        this.repo = repo;
        this.userRepository = userRepository;
        this.db = db;
    }

    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        return Mono.just(solicitud)
                .doOnNext(solicitudes -> log.info("Iniciando guardado de solicitud : {}", solicitudes))
                .flatMap(super::save)
                .as(transactionalService::transactional)
                .doOnNext(solicitudes -> log.info("Solicitud guardada : {}", solicitudes));
    }

    @Override
    public Flux<Solicitud> getSolicitudesByEstado(List<Long> ids) {
        return repository.findByIdEstadoIn(ids)
                .map(e -> mapper.map(e, Solicitud.class));
    }

    @Override
    public Flux<Admin> getAdminsByEstadoNombreIn(List<String> nombres) {
        final String sql = """
        SELECT
            s.id_solicitud,
            s.monto,
            s.plazo,
            s.email,
            e.nombre        AS estado_nombre,
            tp.nombre       AS tipo_prestamo,
            tp.tasa_interes AS tasa_interes,
            (
                SELECT COALESCE(SUM(
                    CASE
                        WHEN tp2.tasa_interes IS NULL OR tp2.tasa_interes = 0
                             THEN s2.monto / NULLIF(s2.plazo, 0)
                        ELSE s2.monto * (
                              tp2.tasa_interes * POWER(1 + tp2.tasa_interes, s2.plazo)
                           ) / (POWER(1 + tp2.tasa_interes, s2.plazo) - 1)
                    END
                ), 0)::numeric(15,2)
                FROM public.solicitudes s2
                JOIN public.tipo_prestamo tp2 ON tp2.id_tipo_prestamo = s2.id_tipo_prestamo
                JOIN public.estados e2       ON e2.id_estado       = s2.id_estado
                WHERE e2.nombre = 'ACEPTADA' AND s2.email = s.email
            ) AS deuda_total_mensual_aprobadas
        FROM public.solicitudes s
        LEFT JOIN public.tipo_prestamo tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo
        LEFT JOIN public.estados      e  ON e.id_estado          = s.id_estado
        WHERE e.nombre = ANY($1)
    """;

        String[] arr = nombres.toArray(new String[0]);

        return db.sql(sql)
                .bind(0, arr)
                .map((row, md) -> {
                    Admin a = new Admin();
                    a.setIdSolicitud(row.get("id_solicitud", Long.class));
                    a.setMonto(row.get("monto", java.math.BigDecimal.class));
                    a.setPlazo(row.get("plazo", Integer.class));
                    a.setEmail(row.get("email", String.class));
                    a.setEstadoSolicitud(row.get("estado_nombre", String.class));
                    a.setTipoPrestamo(row.get("tipo_prestamo", String.class));
                    a.setTasaInteres(row.get("tasa_interes", java.math.BigDecimal.class));
                    a.setDeudaTotalMensualSolicitudesAprobadas(
                            row.get("deuda_total_mensual_aprobadas", java.math.BigDecimal.class)
                    );
                    return a;
                })
                .all()
                .flatMap(a -> userRepository.findByEmailUser(a.getEmail())
                        .map(u -> {
                            a.setNombreUsuario(u.getNombre());
                            a.setSalarioBase(u.getSalarioBase());
                            return a;
                        })
                )
                .doOnNext(a -> log.info("[AdminRow] id={}, tipo={}, tasa={}, estado={}, deudaMes={}",
                        a.getIdSolicitud(), a.getTipoPrestamo(), a.getTasaInteres(),
                        a.getEstadoSolicitud(), a.getDeudaTotalMensualSolicitudesAprobadas()));
    }

    @Override
    public Flux<SolicitudResumenRow> getResumenByEstadoNombreIn(List<String> nombres, int page, int size) {
        long offset = (long) page * size;
        return repo.findResumenByEstadoNombreIn(nombres, offset, size);
    }

    @Override
    public Mono<PageResponse<Admin>> pageAdminsByEstado(
            List<String> estadosNombre, int page, int size, String sort, AdminFilters f) {

        String orderBy = mapSort(sort);

        String base = """
        FROM public.solicitudes s
        JOIN public.estados e ON e.id_estado = s.id_estado
        LEFT JOIN public.tipo_prestamo tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo
        """;

        String select = """
        SELECT
          s.id_solicitud  AS id_solicitud,
          s.monto         AS monto,
          s.plazo         AS plazo,
          s.email         AS email,
          e.nombre        AS estado_nombre,
          tp.nombre       AS tipo_prestamo,
          tp.tasa_interes AS tasa_interes,
          (
            SELECT COALESCE(SUM(
              CASE
                WHEN tp2.tasa_interes IS NULL OR tp2.tasa_interes = 0
                  THEN s2.monto / NULLIF(s2.plazo, 0)
                ELSE s2.monto * ( tp2.tasa_interes * POWER(1 + tp2.tasa_interes, s2.plazo) )
                     / (POWER(1 + tp2.tasa_interes, s2.plazo) - 1)
              END
            ), 0)::numeric(15,2)
            FROM public.solicitudes s2
            JOIN public.tipo_prestamo tp2 ON tp2.id_tipo_prestamo = s2.id_tipo_prestamo
            JOIN public.estados e2       ON e2.id_estado       = s2.id_estado
            WHERE e2.nombre = 'ACEPTADA' AND s2.email = s.email
          ) AS deuda_total
        """ + base;

        String count = "SELECT COUNT(1) AS total " + base;

        StringBuilder where = new StringBuilder();
        List<Object> args = new java.util.ArrayList<>();
        int p = 1;

        // estados: usa ANY($1) con array
        where.append(" WHERE e.nombre = ANY($").append(p).append(") ");
        args.add(estadosNombre.toArray(String[]::new));
        p++;

        if (f != null) {
            if (notBlank(f.getEmailLike())) {
                where.append(" AND LOWER(s.email) LIKE LOWER(CONCAT('%', $").append(p).append(", '%')) ");
                args.add(f.getEmailLike());
                p++;
            }
            if (f.getTipoPrestamoId() != null) {
                where.append(" AND s.id_tipo_prestamo = $").append(p).append(' ');
                args.add(f.getTipoPrestamoId());
                p++;
            }
            if (f.getMinMonto() != null) {
                where.append(" AND s.monto >= $").append(p).append(' ');
                args.add(f.getMinMonto());
                p++;
            }
            if (f.getMaxMonto() != null) {
                where.append(" AND s.monto <= $").append(p).append(' ');
                args.add(f.getMaxMonto());
                p++;
            }
            if (f.getMinPlazo() != null) {
                where.append(" AND s.plazo >= $").append(p).append(' ');
                args.add(f.getMinPlazo());
                p++;
            }
            if (f.getMaxPlazo() != null) {
                where.append(" AND s.plazo <= $").append(p).append(' ');
                args.add(f.getMaxPlazo());
                p++;
            }
        }

        int offset = Math.max(page, 0) * Math.max(size, 1);
        int limit  = Math.max(size, 1);

        String pageSql = select + where + " ORDER BY " + orderBy + " OFFSET $" + p + " LIMIT $" + (p+1);
        List<Object> pageArgs = new java.util.ArrayList<>(args);
        pageArgs.add(offset); // $p
        pageArgs.add(limit);  // $(p+1)

        String countSql = count + where;

        Mono<Long> totalMono = bindPositional(db.sql(countSql), args)
                .map((row, md) -> row.get("total", Long.class))
                .one()
                .defaultIfEmpty(0L);

        Flux<Admin> rows = bindPositional(db.sql(pageSql), pageArgs)
                .map((row, md) -> {
                    Admin a = new Admin();
                    a.setIdSolicitud(row.get("id_solicitud", Long.class));
                    a.setMonto(row.get("monto", java.math.BigDecimal.class));
                    a.setPlazo(row.get("plazo", Integer.class));
                    a.setEmail(row.get("email", String.class));
                    a.setEstadoSolicitud(row.get("estado_nombre", String.class));
                    a.setTipoPrestamo(row.get("tipo_prestamo", String.class));
                    a.setTasaInteres(row.get("tasa_interes", java.math.BigDecimal.class));
                    a.setDeudaTotalMensualSolicitudesAprobadas(row.get("deuda_total", java.math.BigDecimal.class));
                    return a;
                })
                .all()
                .flatMap(a -> userRepository.findByEmailUser(a.getEmail())
                        .map(u -> { a.setNombreUsuario(u.getNombre()); a.setSalarioBase(u.getSalarioBase()); return a; })
                );

        return Mono.zip(rows.collectList(), totalMono)
                .map(tuple -> {
                    var content = tuple.getT1();
                    long total = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) total / Math.max(limit, 1));
                    return PageResponse.<Admin>builder()
                            .content(content)
                            .page(page)
                            .size(limit)
                            .totalElements(total)
                            .totalPages(totalPages)
                            .hasNext(page + 1 < totalPages)
                            .build();
                });
    }

    private static DatabaseClient.GenericExecuteSpec bindPositional(
            DatabaseClient.GenericExecuteSpec spec, List<Object> args) {
        for (int i = 0; i < args.size(); i++) {
            spec = spec.bind(i, args.get(i)); // índice 0-based para $1, $2, ...
        }
        return spec;
    }

    private static boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }


    private static DatabaseClient.GenericExecuteSpec bind(DatabaseClient.GenericExecuteSpec spec, Map<String, Object> params) {
        for (var e : params.entrySet()) {
            spec = spec.bind(e.getKey(), e.getValue());
        }
        return spec;
    }

    // sort: "id,-monto,plazo,-email,-tipo,-estado"
    private static String mapSort(String sort) {
        String def = "s.id_solicitud DESC";
        if (sort == null || sort.isBlank()) return def;

        String field = sort.trim();
        boolean desc = field.startsWith("-");
        if (desc) field = field.substring(1);

        String column = switch (field) {
            case "id", "idSolicitud" -> "s.id_solicitud";
            case "monto" -> "s.monto";
            case "plazo" -> "s.plazo";
            case "email" -> "s.email";
            case "tipo", "tipoPrestamo" -> "tp.nombre";
            case "estado", "estadoSolicitud" -> "e.nombre";
            default -> "s.id_solicitud";
        };
        return column + (desc ? " DESC" : " ASC");
    }
}
