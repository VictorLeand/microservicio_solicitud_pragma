package co.com.pragma.api.rest.tipo;

import co.com.pragma.api.dto.SolicitudDto;
import co.com.pragma.api.dto.TipoPrestamoDto;
import co.com.pragma.api.globalException.GlobalExceptionFilter;
import co.com.pragma.api.path.SolicitudPath;
import co.com.pragma.api.path.TipoPath;
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
public class TipoRouterRest {

    private final TipoPath tipoPath;

    @RouterOperations({
            @RouterOperation(
                    path = "/tipo",
                    method = RequestMethod.POST,
                    beanClass = TipoHandler.class,
                    beanMethod = "listenSaveTipo",
                    operation = @Operation(
                            operationId = "createTipo",
                            summary = "Crear un nuevo tipo",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Tipo creado",
                                            content = @Content(schema = @Schema(implementation = TipoPrestamoDto.class))
                                    )
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunctionTipo(TipoHandler handler, GlobalExceptionFilter filter) {
        return route(POST(tipoPath.getPath()), handler::listenSaveTipo)
                .filter(filter);
    }
}
