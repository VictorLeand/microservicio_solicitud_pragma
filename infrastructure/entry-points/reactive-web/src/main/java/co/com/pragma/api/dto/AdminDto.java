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
@Schema(description = "DTO que representa el estado del prestamos")
public class AdminDto {


    @Schema(description = "Identificador único de la solicitud", example = "12345")
    private Long idSolicitud;

    @Schema(description = "Monto solicitado por el usuario", example = "1500000.50")
    private BigDecimal monto;

    @Schema(description = "Plazo en meses de la solicitud", example = "24")
    private Integer plazo;

    @Schema(description = "Correo electrónico del solicitante", example = "usuario@correo.com")
    private String email;

    @Schema(description = "Nombre completo del solicitante", example = "Juan Pérez")
    private String nombreUsuario;

    @Schema(description = "Nombre del tipo de préstamo asociado", example = "Crédito Hipotecario")
    private String tipoPrestamo;

    @Schema(description = "Tasa de interés aplicada al préstamo (mensual o anual según contexto)", example = "0.025")
    private BigDecimal tasaInteres;

    @Schema(description = "Estado actual de la solicitud", example = "PENDIENTE_REVISION")
    private String estadoSolicitud;

    @Schema(description = "Salario base reportado por el usuario", example = "2800000.00")
    private BigDecimal salarioBase;

    @Schema(description = "Suma total de las deudas mensuales de las solicitudes aprobadas del usuario", example = "450000.00")
    private BigDecimal deudaTotalMensualSolicitudesAprobadas;

}
