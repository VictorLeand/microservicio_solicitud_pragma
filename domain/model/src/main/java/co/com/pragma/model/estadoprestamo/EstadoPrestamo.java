package co.com.pragma.model.estadoprestamo;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EstadoPrestamo {

    private Long id;
    private String nombre;
    private String descripcion;

}
