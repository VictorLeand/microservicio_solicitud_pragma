package co.com.pragma.usecase.estadoprestamo;

import co.com.pragma.model.estadoprestamo.EstadoPrestamo;
import co.com.pragma.model.estadoprestamo.gateways.EstadoPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstadoPrestamoUseCaseTest {

    @Mock
    private EstadoPrestamoRepository repository;

    private EstadoPrestamoUseCase useCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        useCase = new EstadoPrestamoUseCase(repository);
    }

    @Test
    void saveEstado_ok() {
        var estado = new EstadoPrestamo(/* setea campos */);
        when(repository.save(estado)).thenReturn(Mono.just(estado));

        StepVerifier.create(useCase.saveEstado(estado))
                .expectNext(estado)
                .verifyComplete();

        verify(repository).save(estado);
    }

    @Test
    void saveEstado_null_devuelveVacio() {
        StepVerifier.create(useCase.saveEstado(null))
                .verifyComplete();

        verify(repository, never()).save(any());
    }

    @Test
    void findById_ok() {
        Long id = 1L;
        var estado = new EstadoPrestamo(/* setea campos */);
        when(repository.findById(id)).thenReturn(Mono.just(estado));

        StepVerifier.create(useCase.findById(id))
                .expectNext(estado)
                .verifyComplete();

        verify(repository).findById(id);
    }

    @Test
    void findById_noExiste() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findById(id))
                .verifyComplete();

        verify(repository).findById(id);
    }
}