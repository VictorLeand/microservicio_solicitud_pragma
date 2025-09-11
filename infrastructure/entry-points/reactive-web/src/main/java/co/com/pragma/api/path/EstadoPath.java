package co.com.pragma.api.path;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths.estado")
public class EstadoPath {
    private String path;
}

