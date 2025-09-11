package co.com.pragma.r2dbc.config;

import co.com.pragma.model.TokenPayload;
import co.com.pragma.model.enums.Roles;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.gateway.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtTokenVerifier implements TokenProvider {

    @Value("${security.jwt.secret:change-me-change-me-change-me-32-bytes!}")
    private String secret;

    private Key key() { return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }

    @Override
    public String generateToken(Object ignore) { throw new UnsupportedOperationException(); }

    @Override
    public TokenPayload verify(String token) throws BusinessException {
        try {
            Claims c = Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token).getBody();
            return TokenPayload.builder()
                    .subject(c.getSubject())
                    .role(Roles.valueOf(c.get("role", String.class)))
                    .issuedAt(c.getIssuedAt())
                    .expiresAt(c.getExpiration())
                    .build();
        } catch (Exception e) {
            throw new BusinessException("Token inválido o expirado");
        }
    }
}
