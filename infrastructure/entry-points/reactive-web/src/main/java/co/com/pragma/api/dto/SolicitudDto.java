package co.com.pragma.api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa un Solicitud")
public class SolicitudDto {

    @Schema(description = "Monto del usuario", example = "1000000", required = true)
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    @NotNull(message = "El monto es obligatorio")
    private BigDecimal monto;

    @Schema(description = "Plazo del usuario", example = "12", required = true)
    @DecimalMin(value = "0", inclusive = false, message = "El plazo debe ser mayor a 0")
    @NotNull(message = "El plazo es obligatorio")
    private Long plazo;
}
