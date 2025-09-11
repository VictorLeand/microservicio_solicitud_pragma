package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("tipo_prestamo")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TipoPrestamoEntity {

    @Id
    @Column("id_tipo_prestamo")
    private Long id;

    private String nombre;

    @Column("monto_maximo")
    private BigDecimal montoMax;

    @Column("monto_minimo")
    private BigDecimal montoMin;

    @Column("tasa_interes")
    private BigDecimal tasaInteres;

    @Column("validacion_automatica")
    private Boolean validacionAutomatica;

}
