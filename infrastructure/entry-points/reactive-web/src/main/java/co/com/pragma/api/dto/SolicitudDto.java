package co.com.pragma.api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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

    private String document;

    @Schema(description = "Número de cuotas", example = "12", required = true)
    @Min(value = 1, message = "Debe haber al menos una cuota")
    @NotNull(message = "El plazo es obligatorio")
    private Integer plazo;

    @Schema(description = "Correo electrónico del solicitante", example = "user@email.com", required = true)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;

    @Schema(description = "ID del tipo de préstamo", example = "2", required = true)
    @NotNull(message = "El tipo de préstamo es obligatorio")
    private Long idTipoPrestamo;
}
