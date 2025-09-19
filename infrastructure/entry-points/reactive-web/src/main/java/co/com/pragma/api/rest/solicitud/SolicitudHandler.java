package co.com.pragma.api.rest.solicitud;

import co.com.pragma.api.dto.CambioEstadoDto;
import co.com.pragma.api.dto.SolicitudDto;
import co.com.pragma.api.mapper.SolicitudMapper;
import co.com.pragma.api.restclient.UsuariosClient;
import co.com.pragma.api.validator.RequestValidator;
import co.com.pragma.model.exception.BusinessException;

import co.com.pragma.model.solicitud.AdminFilters;
import co.com.pragma.usecase.user.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolicitudHandler {

    private final SolicitudUseCase solicitudUseCase;
    private final RequestValidator requestValidator;
    private final SolicitudMapper mapper;
    private final UsuariosClient usuariosClient;


    public Mono<ServerResponse> listenSaveSolicitud(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SolicitudDto.class)
                .map(requestValidator::validate)
                .map(mapper::toSolicitud)
                .flatMap(solicitudUseCase::saveSolicitud)
                .flatMap(savedSolicitud -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedSolicitud));
    }

    public Mono<ServerResponse> getSolicitudAndUser(ServerRequest serverRequest) {
        return solicitudUseCase.getSolictudesAndUsuarios()
                .collectList()
                .flatMap(getSolicitudAndUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(getSolicitudAndUser));
    }

    public Mono<ServerResponse> crear(ServerRequest req) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .switchIfEmpty(Mono.error(new BusinessException("No autenticado")))
                .flatMap(auth ->
                        req.bodyToMono(SolicitudDto.class)
                                .map(requestValidator::validate)
                                .flatMap(dto -> {
                                    // Regla: el CLIENTE solo puede crear para sí mismo
                                    String emailToken = auth.getName(); // en tu filtro pusiste subject=email
                                    if (!emailToken.equalsIgnoreCase(dto.getEmail())) {
                                        return Mono.error(new BusinessException("Un cliente solo puede crear su propia solicitud"));
                                    }
                                    // Valida usuario en MS Usuarios
                                    return usuariosClient.validarUsuario(dto.getEmail(), dto.getDocument())
                                            .flatMap(ok -> {
                                                if (!ok) return Mono.error(new BusinessException("Usuario no válido"));
                                                // Persiste la solicitud
                                                return solicitudUseCase.saveSolicitud(mapper.toSolicitud(dto));
                                            });
                                })
                )
                .flatMap(saved -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(mapper.toResponse(saved)))
                .onErrorResume(BusinessException.class,
                        e -> ServerResponse.status(HttpStatus.FORBIDDEN).bodyValue(
                                java.util.Map.of("error", e.getMessage())));
    }

    public Mono<ServerResponse> listarPendientes(ServerRequest req) {
        int page = parseInt(req.queryParam("page").orElse("0"), 0);
        int size = Math.min(parseInt(req.queryParam("size").orElse("10"), 10), 100);
        String sort = req.queryParam("sort").orElse("-id"); // por defecto id desc

        AdminFilters f = AdminFilters.builder()
                .emailLike(req.queryParam("email").orElse(null))
                .tipoPrestamoId(req.queryParam("tipoId").map(Long::valueOf).orElse(null))
                .minMonto(req.queryParam("minMonto").map(java.math.BigDecimal::new).orElse(null))
                .maxMonto(req.queryParam("maxMonto").map(java.math.BigDecimal::new).orElse(null))
                .minPlazo(req.queryParam("minPlazo").map(Integer::valueOf).orElse(null))
                .maxPlazo(req.queryParam("maxPlazo").map(Integer::valueOf).orElse(null))
                .build();

        return solicitudUseCase.listarPendientes(page, size, sort, f)
                .flatMap(p -> ServerResponse.ok()
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .bodyValue(p));
    }

    public Mono<ServerResponse> actualizarEstado(ServerRequest req) {
        Long id = Long.valueOf(req.pathVariable("id"));
        return req.bodyToMono(CambioEstadoDto.class)
                .map(requestValidator::validate)
                .flatMap(dto -> solicitudUseCase.ejecutar(id, dto.getEstado()))
                .flatMap(sol -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(mapper.toResponse(sol)))
                .onErrorResume(BusinessException.class, e ->
                        ServerResponse.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                                .bodyValue(java.util.Map.of("error", e.getMessage())));
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
