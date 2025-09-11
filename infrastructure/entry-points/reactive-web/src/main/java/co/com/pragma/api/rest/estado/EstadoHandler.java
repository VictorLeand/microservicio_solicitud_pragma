package co.com.pragma.api.rest.estado;

import co.com.pragma.api.dto.EstadoPrestamoDto;
import co.com.pragma.api.mapper.EstadoMapper;
import co.com.pragma.api.validator.RequestValidator;
import co.com.pragma.usecase.estadoprestamo.EstadoPrestamoUseCase;
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
public class EstadoHandler {

    private final EstadoPrestamoUseCase estadoPrestamoUseCase;
    private final RequestValidator requestValidator;
    private final EstadoMapper mapper;

    public Mono<ServerResponse> listenSaveEstado(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EstadoPrestamoDto.class)
                .map(requestValidator::validate)
                .map(mapper::toEstado)
                .flatMap(estadoPrestamoUseCase::saveEstado)
                .flatMap(savedEstado -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedEstado));
    }
}
