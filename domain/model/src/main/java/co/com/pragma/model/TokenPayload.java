package co.com.pragma.model;

import co.com.pragma.model.enums.Roles;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TokenPayload {

    private String subject;
    private Long   id;
    private Roles role;
    private Date issuedAt;
    private Date expiresAt;
}
