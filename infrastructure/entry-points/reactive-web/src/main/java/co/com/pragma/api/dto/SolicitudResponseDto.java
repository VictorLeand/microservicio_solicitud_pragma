package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(
        name = "SolicitudResponse",
        description = "Representa la respuesta de una solicitud creada/consultada."
)
public class SolicitudResponseDto {

    @Schema(
            description = "Identificador único de la solicitud",
            example = "12345",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Monto solicitado por el cliente",
            example = "1500000.50",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private BigDecimal monto;

    @Schema(
            description = "Número de cuotas (plazo en meses)",
            example = "12",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Integer plazo;

    @Schema(
            description = "Correo electrónico del solicitante",
            example = "cliente1@acme.com",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String email;

    @Schema(
            description = "ID del estado actual de la solicitud",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idEstado;

    @Schema(
            description = "ID del tipo de préstamo asociado a la solicitud",
            example = "2",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idTipoPrestamo;
}
