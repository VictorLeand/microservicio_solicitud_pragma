package co.com.pragma.model.lambdas;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionMensaje {

    private String email;
    private String asunto;
    private String mensaje;
    private String estado;
}
