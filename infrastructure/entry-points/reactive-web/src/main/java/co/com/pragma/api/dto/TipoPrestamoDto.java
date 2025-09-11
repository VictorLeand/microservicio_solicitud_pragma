package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa un tipo de préstamo con sus condiciones")
public class TipoPrestamoDto {

    @Schema(description = "Nombre del tipo de préstamo", example = "Crédito Hipotecario")
    private String nombre;

    @Schema(description = "Monto máximo permitido para el préstamo", example = "50000000")
    private BigDecimal montoMax;

    @Schema(description = "Monto mínimo permitido para el préstamo", example = "1000000")
    private BigDecimal montoMin;

    @Schema(description = "Tasa de interés anual en porcentaje", example = "12.5")
    private BigDecimal tasaInteres;

}
