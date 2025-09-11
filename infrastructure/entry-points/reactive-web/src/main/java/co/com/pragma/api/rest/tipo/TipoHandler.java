package co.com.pragma.api.rest.tipo;

import co.com.pragma.api.dto.SolicitudDto;
import co.com.pragma.api.dto.TipoPrestamoDto;
import co.com.pragma.api.mapper.SolicitudMapper;
import co.com.pragma.api.mapper.TipoMapper;
import co.com.pragma.api.validator.RequestValidator;
import co.com.pragma.usecase.tipoprestamo.TipoPrestamoUseCase;
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
public class TipoHandler {

    private final TipoPrestamoUseCase tipoPrestamoUseCase;
    private final RequestValidator requestValidator;
    private final TipoMapper mapper;

    public Mono<ServerResponse> listenSaveTipo(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TipoPrestamoDto.class)
                .map(requestValidator::validate)
                .map(mapper::toTipo)
                .flatMap(tipoPrestamoUseCase::saveTipo)
                .flatMap(savedTipo -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedTipo));
    }
}
