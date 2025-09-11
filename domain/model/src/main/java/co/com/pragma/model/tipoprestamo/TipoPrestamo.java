package co.com.pragma.model.tipoprestamo;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TipoPrestamo {

    private Long id;
    private String nombre;
    private BigDecimal montoMax;
    private BigDecimal montoMin;
    private BigDecimal tasaInteres;
    private Boolean validacionAutomatica;

}
