package co.com.pragma.api.capacidad;

import co.com.pragma.api.globalException.GlobalExceptionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CapacidadRouter {

    @Bean
    @RouterOperation(
            path = "/api/v1/calcular-capacidad/{idSolicitud}",
            method = RequestMethod.POST,
            operation = @Operation(
                    summary = "Encolar validación automática de capacidad",
                    responses = {@ApiResponse(responseCode = "202", description = "Encolado")}
            )
    )
    public RouterFunction<ServerResponse> capacidadRoutes(CapacidadHandler h, GlobalExceptionFilter f) {
        return route(POST("/api/v1/calcular-capacidad/{idSolicitud}"), h::disparar)
                .filter(f);
    }
}
