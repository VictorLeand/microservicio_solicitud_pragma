package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<Boolean> existsByEmailAndByDocument(String email, String document);

    Mono<User> findByEmailUser(String email);

}
