package co.com.pragma.r2dbc.transaccion;

import co.com.pragma.model.transactional.TransactionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Transactional implements TransactionalService {

    private final TransactionalOperator transactionalOperator;

    @Override
    public <T> Mono<T> transactional(Mono<T> publisher) {
        return transactionalOperator.transactional(publisher);
    }

}
