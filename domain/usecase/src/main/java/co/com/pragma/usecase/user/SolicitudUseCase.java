package co.com.pragma.usecase.user;


import co.com.pragma.model.lambdas.NotificacionMensaje;
import co.com.pragma.model.login.PageResponse;
import co.com.pragma.model.admin.Admin;
import co.com.pragma.model.estadoprestamo.gateways.EstadoPrestamoRepository;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.gateway.NotificacionPublisher;
import co.com.pragma.model.solicitud.AdminFilters;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UserRepository userRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoPrestamoRepository estadoPrestamoRepository;
    private final NotificacionPublisher notificacionPublisher;

    public SolicitudUseCase(SolicitudRepository solicitudRepository, UserRepository userRepository, TipoPrestamoRepository tipoPrestamoRepository, EstadoPrestamoRepository estadoPrestamoRepository, NotificacionPublisher notificacionPublisher) {
        this.solicitudRepository = solicitudRepository;
        this.userRepository = userRepository;
        this.tipoPrestamoRepository = tipoPrestamoRepository;
        this.estadoPrestamoRepository = estadoPrestamoRepository;
        this.notificacionPublisher = notificacionPublisher;
    }

    public Mono<Solicitud> saveSolicitud(Solicitud solicitud) {
        return Mono.justOrEmpty(solicitud)
                .switchIfEmpty(Mono.error(new BusinessException("La solicitud es null")))
                .flatMap(s -> userRepository.existsByEmailAndByDocument(s.getEmail(), s.getDocument())
                        .flatMap(exists -> {
                            if (!exists) return Mono.error(new BusinessException("El usuario no existe"));
                            return tipoPrestamoRepository.findById(s.getIdTipoPrestamo())
                                    .switchIfEmpty(Mono.error(new BusinessException("El tipo de préstamo no existe")))
                                    .flatMap(tp -> validarYGuardar(s));
                        })
                );
    }

    public Flux<Admin> getSolictudesAndUsuarios(){
        List<String> estados = List.of("PENDIENTE DE REVISIÓN", "RECHAZADA", "REVISIÓN MANUAL", "ACEPTADA");
        return solicitudRepository.getAdminsByEstadoNombreIn(estados);
    }

    private Mono<Solicitud> validarYGuardar(Solicitud s) {
        // siempre forzar “PENDIENTE DE REVISIÓN”
        return estadoPrestamoRepository.findIdByNombre("PENDIENTE DE REVISIÓN")
                .switchIfEmpty(Mono.error(new BusinessException(
                        "No está configurado el estado 'PENDIENTE DE REVISIÓN' en la tabla estados")))
                .flatMap(idPendiente -> {
                    s.setIdEstado(idPendiente);
                    return solicitudRepository.save(s);
                });
    }

    public Mono<PageResponse<Admin>> listarPendientes(
            int page, int size, String sort, AdminFilters filters) {

        // Estados que quedan "a decisión del admin"
        var estados = java.util.List.of("PENDIENTE DE REVISIÓN", "REVISIÓN MANUAL");

        return solicitudRepository.pageAdminsByEstado(
                estados, page, size, sort, filters);
    }

    public Mono<Solicitud> ejecutar(Long idSolicitud, String nuevoEstadoNombre) {
        if (idSolicitud == null) return Mono.error(new BusinessException("Id de solicitud es requerido"));
        if (!"ACEPTADA".equalsIgnoreCase(nuevoEstadoNombre) && !"RECHAZADA".equalsIgnoreCase(nuevoEstadoNombre)) {
            return Mono.error(new BusinessException("Estado inválido. Use ACEPTADA o RECHAZADA"));
        }

        final String estadoNormalizado = nuevoEstadoNombre.trim().toUpperCase();

        return solicitudRepository.findById(idSolicitud)
                .switchIfEmpty(Mono.error(new BusinessException("Solicitud no encontrada")))
                .flatMap(sol ->
                        estadoPrestamoRepository.findIdByNombre(estadoNormalizado)
                                .switchIfEmpty(Mono.error(new BusinessException("Estado no configurado: " + estadoNormalizado)))
                                .flatMap(idEstado -> solicitudRepository.updateEstadoById(idSolicitud, idEstado)
                                        .flatMap(rows -> {
                                            if (rows == 0) return Mono.error(new BusinessException("No se pudo actualizar el estado"));

                                            NotificacionMensaje msg = NotificacionMensaje.builder()
                                                    .email(sol.getEmail())
                                                    .estado(estadoNormalizado)
                                                    .asunto("Notificación de estado de prestamo") // opcional
                                                    .build();

                                            return notificacionPublisher.publicar(msg)
                                                    .onErrorResume(ex -> {
                                                        System.err.println("[Notificacion] Error publicando: " + ex.getMessage());
                                                        return Mono.empty();
                                                    })
                                                    .thenReturn(sol.toBuilder().idEstado(idEstado).build());
                                        })
                                )
                );
        }
    }



