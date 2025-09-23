package co.com.pragma.usecase.tipoprestamo;

import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TipoPrestamoUseCaseTest {

    @Mock
    private TipoPrestamoRepository repository;

    private TipoPrestamoUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new TipoPrestamoUseCase(repository);
    }

    @Test
    void saveTipo_debeRetornarEntidadGuardada_cuandoRepositorioRespondeOK() {
        // Arrange
        TipoPrestamo entrada = new TipoPrestamo(); // setea campos si hace falta
        TipoPrestamo guardado = new TipoPrestamo(); // simula la respuesta del repo
        when(repository.save(entrada)).thenReturn(Mono.just(guardado));

        // Act
        Mono<TipoPrestamo> result = useCase.saveTipo(entrada);

        // Assert
        StepVerifier.create(result)
                .expectNext(guardado)
                .verifyComplete();

        verify(repository, times(1)).save(entrada);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void saveTipo_debeCompletarVacio_cuandoEntradaEsNull_conJustOrEmpty() {
        // Arrange
        // (no hay nada que preparar)

        // Act
        Mono<TipoPrestamo> result = useCase.saveTipo(null);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verifyNoInteractions(repository);
    }

}