package co.com.pragma.api.capacidad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CapacidadInternaRouter {

    @Bean
    public RouterFunction<ServerResponse> capacidadInternaRoutes(CapacidadInternaHandler h) {
        return route(GET("/internal/solicitudes/deuda-mensual"), h::deudaMensual)
                .andRoute(POST("/internal/solicitudes/{id}/decision"), h::callback);
    }
}
