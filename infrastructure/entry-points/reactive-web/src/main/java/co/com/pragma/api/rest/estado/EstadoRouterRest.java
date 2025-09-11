package co.com.pragma.api.rest.estado;

import co.com.pragma.api.dto.EstadoPrestamoDto;
import co.com.pragma.api.globalException.GlobalExceptionFilter;
import co.com.pragma.api.path.EstadoPath;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class EstadoRouterRest {

    private final EstadoPath estadoPath;

    @RouterOperations({
            @RouterOperation(
                    path = "/estado",
                    method = RequestMethod.POST,
                    beanClass = EstadoHandler.class,
                    beanMethod = "listenSaveEstado",
                    operation = @Operation(
                            operationId = "createEstado",
                            summary = "Crear un nuevo estado",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Estado creado",
                                            content = @Content(schema = @Schema(implementation = EstadoPrestamoDto.class))
                                    )
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunctionEstado(EstadoHandler handler, GlobalExceptionFilter filter) {
        return route(POST(estadoPath.getPath()), handler::listenSaveEstado)
                .filter(filter);
    }
}
