package co.com.pragma.model.transactional;

import reactor.core.publisher.Mono;

public interface TransactionalService {

    <T> Mono<T> transactional(Mono<T> publisher);
}

