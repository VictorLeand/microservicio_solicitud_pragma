package co.com.pragma.model.admin;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Admin {

    private Long idSolicitud;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private String nombreUsuario;
    private String tipoPrestamo;
    private BigDecimal tasaInteres;
    private String estadoSolicitud;
    private BigDecimal salarioBase;
    private BigDecimal deudaTotalMensualSolicitudesAprobadas;
}
