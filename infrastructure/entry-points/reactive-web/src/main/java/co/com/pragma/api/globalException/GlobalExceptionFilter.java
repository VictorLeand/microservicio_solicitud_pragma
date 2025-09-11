package co.com.pragma.api.globalException;

import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Slf4j
public class GlobalExceptionFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request)
                .onErrorResume(BusinessException.class, ex -> {
                    log.warn("BusinessException: {}", ex.getMessage());

                    ExceptionResponse response = new ExceptionResponse(
                            LocalDateTime.now(),
                            ex.getMessage(),
                            request.path()
                    );

                    return ServerResponse
                            .status(HttpStatus.BAD_REQUEST)
                            .bodyValue(response);
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Unhandled exception: {}", ex.getMessage(), ex);

                    ExceptionResponse response = new ExceptionResponse(
                            LocalDateTime.now(),
                            "Error interno del servidor",
                            request.path()
                    );

                    return ServerResponse
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue(response);
                });
    }
}
