package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("estados")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EstadoPrestamoEntity {

    @Id
    @Column("id_estado")
    private Long id;
    private String nombre;
    private String descripcion;
}
