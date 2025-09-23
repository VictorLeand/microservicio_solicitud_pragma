package co.com.pragma.usecase.capacidad;

import co.com.pragma.model.capacidad.CalcularCapacidadRequest;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.gateway.CapacidadPublisher;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalcularCapacidadUseCaseTest {

    @Mock
    SolicitudRepository solicitudRepository;

    @Mock
    CapacidadPublisher capacidadPublisher;

    @InjectMocks
    CalcularCapacidadUseCase useCase;

    @Test
    void solicitarValidacion_debeFallar_cuandoSolicitudNoExiste() {
        // Arrange
        Long id = 99L;
        when(solicitudRepository.findById(id)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = useCase.solicitarValidacion(id);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof BusinessException);
                    assertEquals("Solicitud no existe", ex.getMessage());
                })
                .verify();

        verify(solicitudRepository).findById(id);
        verifyNoMoreInteractions(solicitudRepository);
        verifyNoInteractions(capacidadPublisher);
    }

    @Test
    void solicitarValidacion_ok_conDeuda() {
        // Arrange
        Long id = 10L;
        String email = "user@test.com";
        BigDecimal monto = new BigDecimal("5000000");
        Integer plazo = 36;
        BigDecimal deuda = new BigDecimal("123.45");

        // Usamos un mock de Solicitud para no depender del builder real
        Solicitud sol = mock(Solicitud.class);
        when(sol.getId()).thenReturn(id);
        when(sol.getEmail()).thenReturn(email);
        when(sol.getMonto()).thenReturn(monto);
        when(sol.getPlazo()).thenReturn(plazo);

        when(solicitudRepository.findById(id)).thenReturn(Mono.just(sol));
        when(solicitudRepository.deudaMensualAprobadas(email)).thenReturn(Mono.just(deuda));
        when(capacidadPublisher.publicar(any(CalcularCapacidadRequest.class)))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = useCase.solicitarValidacion(id);

        // Assert
        StepVerifier.create(result).verifyComplete();

        ArgumentCaptor<CalcularCapacidadRequest> reqCap = ArgumentCaptor.forClass(CalcularCapacidadRequest.class);
        verify(capacidadPublisher).publicar(reqCap.capture());
        CalcularCapacidadRequest sent = reqCap.getValue();

        assertEquals(id, sent.getIdSolicitud());
        assertEquals(email, sent.getEmail());
        assertEquals(monto, sent.getMonto());
        assertEquals(plazo, sent.getPlazo());
        assertEquals(deuda, sent.getDeudaMensualActual());
        assertNull(sent.getTasaInteres());   // como en tu caso de uso
        assertNull(sent.getIngresosTotales()); // idem

        verify(solicitudRepository).findById(id);
        verify(solicitudRepository).deudaMensualAprobadas(email);
    }

    @Test
    void solicitarValidacion_ok_sinDeuda_usaCeroPorDefecto() {
        // Arrange
        Long id = 20L;
        String email = "no-deuda@test.com";
        BigDecimal monto = new BigDecimal("1000000");
        Integer plazo = 12;

        Solicitud sol = mock(Solicitud.class);
        when(sol.getId()).thenReturn(id);
        when(sol.getEmail()).thenReturn(email);
        when(sol.getMonto()).thenReturn(monto);
        when(sol.getPlazo()).thenReturn(plazo);

        when(solicitudRepository.findById(id)).thenReturn(Mono.just(sol));
        // defaultIfEmpty(BigDecimal.ZERO) debe cubrir este caso
        when(solicitudRepository.deudaMensualAprobadas(email)).thenReturn(Mono.empty());
        when(capacidadPublisher.publicar(any(CalcularCapacidadRequest.class)))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = useCase.solicitarValidacion(id);

        // Assert
        StepVerifier.create(result).verifyComplete();

        ArgumentCaptor<CalcularCapacidadRequest> reqCap = ArgumentCaptor.forClass(CalcularCapacidadRequest.class);
        verify(capacidadPublisher).publicar(reqCap.capture());
        CalcularCapacidadRequest sent = reqCap.getValue();

        assertEquals(new BigDecimal("0"), sent.getDeudaMensualActual());

        verify(solicitudRepository).findById(id);
        verify(solicitudRepository).deudaMensualAprobadas(email);
    }

    @Test
    void solicitarValidacion_errorEnPublisher_sePropaga() {
        // Arrange
        Long id = 30L;
        String email = "err@test.com";

        Solicitud sol = mock(Solicitud.class);
        when(sol.getId()).thenReturn(id);
        when(sol.getEmail()).thenReturn(email);
        when(sol.getMonto()).thenReturn(new BigDecimal("2500000"));
        when(sol.getPlazo()).thenReturn(24);

        when(solicitudRepository.findById(id)).thenReturn(Mono.just(sol));
        when(solicitudRepository.deudaMensualAprobadas(email)).thenReturn(Mono.just(new BigDecimal("50")));
        when(capacidadPublisher.publicar(any(CalcularCapacidadRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("SNS caído")));

        // Act
        Mono<Void> result = useCase.solicitarValidacion(id);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> assertEquals("SNS caído", ex.getMessage()))
                .verify();

        verify(capacidadPublisher).publicar(any(CalcularCapacidadRequest.class));
    }
}
