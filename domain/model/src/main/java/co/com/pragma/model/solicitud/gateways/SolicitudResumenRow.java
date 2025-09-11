package co.com.pragma.model.solicitud.gateways;

import java.math.BigDecimal;

public interface SolicitudResumenRow {

    Long getIdSolicitud();
    BigDecimal getMonto();
    Integer getPlazo();
    String getEmail();
    Long getIdEstado();
    String getEstadoNombre();
    String getTipoPrestamo();
    BigDecimal getTasaInteres();
}

