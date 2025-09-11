package co.com.pragma.model.user;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Long id;
    private String nombre;
    private String apellido;
    private String document;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String email;
    private BigDecimal salarioBase;
}
