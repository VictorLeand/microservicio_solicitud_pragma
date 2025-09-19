package co.com.pragma.model.capacidad;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Map;

@Value
@Builder(toBuilder = true)
public class CalcularCapacidadDecision {

    Long idSolicitud;
    String decision;
    BigDecimal cuotaPrestamoNuevo;
    BigDecimal capacidadDisponible;
    BigDecimal deudaMensualActual;
    Map<String, Object> debug;
}
