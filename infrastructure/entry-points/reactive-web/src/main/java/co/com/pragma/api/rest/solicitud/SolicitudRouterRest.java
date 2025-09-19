package co.com.pragma.api.rest.solicitud;

import co.com.pragma.api.dto.AdminDto;
import co.com.pragma.api.dto.SolicitudResponseDto;
import co.com.pragma.api.path.SolicitudPath;
import co.com.pragma.api.dto.SolicitudDto;
import co.com.pragma.api.globalException.GlobalExceptionFilter;
import co.com.pragma.model.login.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class SolicitudRouterRest {

    private final SolicitudPath solicitudPath;

    @RouterOperations({

            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.POST,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "crear",
                    operation = @Operation(
                            operationId = "createSolicitud",
                            summary = "Crear una nueva solicitud",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de la solicitud a crear",
                                    content = @Content(schema = @Schema(implementation = SolicitudDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Solicitud creada correctamente",
                                            content = @Content(schema = @Schema(implementation = SolicitudResponseDto.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Petición inválida (validación de campos)",
                                            content = @Content
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "No autenticado",
                                            content = @Content
                                    ),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "Prohibido (ej. el cliente intenta crear para otro email, usuario no válido)",
                                            content = @Content
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Recursos asociados no encontrados (p. ej. tipo de préstamo)",
                                            content = @Content
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno",
                                            content = @Content
                                    )
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.GET,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "getSolicitudAndUser",
                    operation = @Operation(
                            operationId = "listSolicitudesWithUser",
                            summary = "Listar solicitudes con datos de usuario",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Listado obtenido",
                                            content = @Content(
                                                    array = @ArraySchema(schema = @Schema(implementation = AdminDto.class))
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "No autenticado",
                                            content = @Content
                                    ),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "Prohibido",
                                            content = @Content
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno",
                                            content = @Content
                                    )
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/solicitud/admin",
                    method = RequestMethod.GET,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "listarPendientes",
                    operation = @Operation(
                            operationId = "pageSolicitudesPendientes",
                            summary = "Listar solicitudes pendientes (paginado y filtros para admin)",
                            parameters = {
                                    @Parameter(name = "page", description = "Número de página (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                                    @Parameter(name = "size", description = "Tamaño de página", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
                                    @Parameter(name = "sort", description = "Campo de orden (ej: id, -monto, plazo, -email, tipo, estado)", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
                                    @Parameter(name = "emailLike", description = "Filtro por coincidencia de email", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
                                    @Parameter(name = "tipoPrestamoId", description = "Filtro por ID de tipo de préstamo", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
                                    @Parameter(name = "minMonto", description = "Monto mínimo", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "bigdecimal")),
                                    @Parameter(name = "maxMonto", description = "Monto máximo", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "bigdecimal")),
                                    @Parameter(name = "minPlazo", description = "Plazo mínimo (meses)", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
                                    @Parameter(name = "maxPlazo", description = "Plazo máximo (meses)", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Página de solicitudes pendientes",
                                            // Si usas un PageResponse<AdminDto>, especifica ese wrapper;
                                            // si no tienes DTO de página, al menos documenta la estructura:
                                            content = @Content(schema = @Schema(implementation = PageResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "No autenticado",
                                            content = @Content
                                    ),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "Prohibido (solo ADMIN/ASESOR)",
                                            content = @Content
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno",
                                            content = @Content
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud/{id}/estado",
                    method = RequestMethod.PUT,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "actualizarEstado",
                    operation = @Operation(
                            operationId = "aprobarRechazarSolicitud",
                            summary = "Cambiar estado de solicitud a ACEPTADA o RECHAZADA (solo ASESOR)",
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = co.com.pragma.api.dto.CambioEstadoDto.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Estado actualizado"),
                                    @ApiResponse(responseCode = "400", description = "Error de negocio/validación"),
                                    @ApiResponse(responseCode = "403", description = "Prohibido"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(SolicitudHandler handler, GlobalExceptionFilter filter) {
        return route(POST(solicitudPath.getPath()), handler::crear)
                .andRoute(GET(solicitudPath.getPath()), handler::getSolicitudAndUser)
                .andRoute(GET("/api/v1/solicitud/admin"), handler::listarPendientes)
                .andRoute(PUT("/api/v1/solicitud/{id}/estado"), handler::actualizarEstado)
                .filter(filter);
    }
}
