package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.PageResponse;
import co.com.pragma.model.admin.Admin;
import co.com.pragma.model.solicitud.AdminFilters;
import co.com.pragma.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SolicitudRepository {

    Mono<Solicitud> save(Solicitud solicitud);

    Flux<SolicitudResumenRow> getResumenByEstadoNombreIn(List<String> nombres, int page, int size);

    Flux<Solicitud> getSolicitudesByEstado(List<Long> ids);

    Flux<Admin> getAdminsByEstadoNombreIn(List<String> nombres);

    Mono<PageResponse<Admin>> pageAdminsByEstado(List<String> estados, int page, int size, String sort, AdminFilters filters);
}
