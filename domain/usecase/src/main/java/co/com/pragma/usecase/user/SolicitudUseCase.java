package co.com.pragma.usecase.user;


import co.com.pragma.model.PageResponse;
import co.com.pragma.model.admin.Admin;
import co.com.pragma.model.estadoprestamo.gateways.EstadoPrestamoRepository;
import co.com.pragma.model.exception.BusinessException;
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

    public SolicitudUseCase(SolicitudRepository solicitudRepository, UserRepository userRepository, TipoPrestamoRepository tipoPrestamoRepository, EstadoPrestamoRepository estadoPrestamoRepository) {
        this.solicitudRepository = solicitudRepository;
        this.userRepository = userRepository;
        this.tipoPrestamoRepository = tipoPrestamoRepository;
        this.estadoPrestamoRepository = estadoPrestamoRepository;
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
}



