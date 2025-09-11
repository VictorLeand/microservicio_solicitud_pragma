package co.com.pragma.model;


import co.com.pragma.model.enums.Roles;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Login {

    private Long id;
    private String email;
    private String password;
    private Roles rol;
}
