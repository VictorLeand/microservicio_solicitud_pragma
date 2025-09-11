package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa el estado del prestamos")
public class EstadoPrestamoDto {

    @Schema(description = "Nombre del estado del préstamo", example = "APROBADO", required = true)
    private String nombre;

    @Schema(description = "Descripción detallada del estado", example = "El préstamo fue aprobado y está en desembolso", required = true)
    private String descripcion;

}
