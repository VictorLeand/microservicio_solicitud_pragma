package co.com.pragma.api;

import co.com.pragma.api.dto.SolicitudDto;
import co.com.pragma.api.mapper.SolicitudMapper;
import co.com.pragma.api.validator.RequestValidator;
import co.com.pragma.usecase.user.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final SolicitudUseCase solicitudUseCase;
    private final RequestValidator requestValidator;
    private final SolicitudMapper mapper;

    public Mono<ServerResponse> listenSaveSolicitud(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SolicitudDto.class)
                .map(requestValidator::validate)
                .map(mapper::toSolicitud)
                .flatMap(solicitudUseCase::saveSolicitud)
                .flatMap(savedSolicitud -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedSolicitud));
    }
}
