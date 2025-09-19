package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CambioEstadoDto {

    @Schema(example = "ACEPTADA", description = "Nuevo estado: ACEPTADA o RECHAZADA")
    @NotBlank
    private String estado;
}