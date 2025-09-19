package co.com.pragma.r2dbc.config;

import co.com.pragma.model.login.TokenPayload;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.gateway.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final TokenProvider tokenProvider;

    public JwtAuthenticationFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                TokenPayload payload = tokenProvider.verify(token);
                String roleName = payload.getRole().name(); // ADMIN | ASESOR | CLIENTE
                if (roleName.startsWith("ROLE_")) roleName = roleName.substring(5);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        payload.getSubject(), token, List.of(new SimpleGrantedAuthority("ROLE_" + roleName)));

                // Log útil
                System.out.println("[Solicitudes][JWT] subject=" + payload.getSubject()
                        + " authorities=" + authentication.getAuthorities()
                        + " path=" + exchange.getRequest().getMethod() + " " + exchange.getRequest().getPath());

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } catch (BusinessException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        return chain.filter(exchange);
    }
}
