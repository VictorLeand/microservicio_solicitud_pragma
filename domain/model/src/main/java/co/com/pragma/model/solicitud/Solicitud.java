package co.com.pragma.model.solicitud;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {

    private Long id;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Long idEstado;
    private Long idTipoPrestamo;
    private String document;

}
