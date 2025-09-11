package co.com.pragma.r2dbc.entity;

import co.com.pragma.model.estadoprestamo.EstadoPrestamo;
import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("solicitudes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SolicitudEntity {

    @Id
    @Column("id_solicitud")
    private Long id;

    private BigDecimal monto;
    private Integer plazo;
    private String email;

    @Column("id_estado")
    private Long idEstado;

    @Column("id_tipo_prestamo")
    private Long idTipoPrestamo;


    @Transient
    private TipoPrestamo tipoPrestamo;

    @Transient
    private EstadoPrestamo estadoPrestamo;

}
