package co.com.pragma.model.user;

import java.math.BigDecimal;

public class Solicitud {

    private Long id;
    private BigDecimal monto;
    private Long plazo;

    public Solicitud(Long id, BigDecimal monto, Long plazo) {
        this.id = id;
        this.monto = monto;
        this.plazo = plazo;
    }

    public Solicitud() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Long getPlazo() {
        return plazo;
    }

    public void setPlazo(Long plazo) {
        this.plazo = plazo;
    }
}
