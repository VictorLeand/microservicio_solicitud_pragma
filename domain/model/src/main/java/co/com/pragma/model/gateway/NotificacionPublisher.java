package co.com.pragma.model.gateway;

import co.com.pragma.model.lambdas.NotificacionMensaje;
import reactor.core.publisher.Mono;

public interface NotificacionPublisher {

    Mono<Void> publicar(NotificacionMensaje mensaje);
}
