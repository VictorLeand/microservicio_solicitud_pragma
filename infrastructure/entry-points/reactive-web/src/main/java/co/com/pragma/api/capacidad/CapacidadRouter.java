package co.com.pragma.api.capacidad;

import co.com.pragma.api.globalException.GlobalExceptionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CapacidadRouter {

    public static final String BASE = "/api/v1/calcular-capacidad";

    @Bean
    @RouterOperation(
            path = BASE + "/{idSolicitud}",
            method = RequestMethod.POST,
            operation = @Operation(
                    summary = "Encolar validación automática de capacidad",
                    responses = {
                            @ApiResponse(responseCode = "202", description = "Encolado",
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Map.class))),
                            @ApiResponse(responseCode = "400", description = "Id inválido"),
                            @ApiResponse(responseCode = "404", description = "Solicitud no existe"),
                            @ApiResponse(responseCode = "500", description = "Error interno")
                    }
            )
    )
    public RouterFunction<ServerResponse> capacidadRoutes(CapacidadHandler h, GlobalExceptionFilter f) {
        return route(POST(BASE + "/{idSolicitud}"), h::disparar)
                .filter(f);
    }
}
