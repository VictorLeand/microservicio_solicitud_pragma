package co.com.pragma.model.capacidad;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class CalcularCapacidadRequest {

    Long idSolicitud;
    String email;
    BigDecimal monto;
    Integer plazo;
    BigDecimal tasaInteres;
    BigDecimal deudaMensualActual;
    BigDecimal ingresosTotales;
}
