package co.com.pragma.api.rest.solicitud;

import co.com.pragma.api.globalException.GlobalExceptionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SolicitudEstadoRouter {

    @Bean
    public RouterFunction<ServerResponse> solicitudEstadoRoutes(
            SolicitudEstadoHandler h, GlobalExceptionFilter f) {
        return route(PUT("/api/v1/solicitud/{id}"), h::actualizar).filter(f);
    }
}
