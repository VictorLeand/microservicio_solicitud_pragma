package co.com.pragma.model.admin;


import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.user.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ResponseDto {

    private Solicitud solicitud;
    private User user;
}
