package co.com.pragma.usecase.user;

import co.com.pragma.model.admin.Admin;
import co.com.pragma.model.estadoprestamo.gateways.EstadoPrestamoRepository;
import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.gateway.NotificacionPublisher;
import co.com.pragma.model.login.PageResponse;
import co.com.pragma.model.solicitud.AdminFilters;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;
    @Mock
    private EstadoPrestamoRepository estadoPrestamoRepository;
    @Mock
    private NotificacionPublisher notificacionPublisher;

    private SolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new SolicitudUseCase(
                solicitudRepository,
                userRepository,
                tipoPrestamoRepository,
                estadoPrestamoRepository,
                notificacionPublisher
        );
    }

    // ---------- saveSolicitud ----------

    @Test
    void saveSolicitud_debeFallar_cuandoSolicitudEsNull() {
        // Arrange

        // Act
        Mono<Solicitud> result = useCase.saveSolicitud(null);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(ex.getMessage()).isEqualTo("La solicitud es null");
                })
                .verify();

        verifyNoInteractions(userRepository, tipoPrestamoRepository, estadoPrestamoRepository, solicitudRepository, notificacionPublisher);
    }

    @Test
    void saveSolicitud_debeFallar_cuandoUsuarioNoExiste() {
        // Arrange
        Solicitud s = buildSolicitudDemo();

        when(userRepository.existsByEmailAndByDocument(s.getEmail(), s.getDocument()))
                .thenReturn(Mono.just(false));

        // Act
        Mono<Solicitud> result = useCase.saveSolicitud(s);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(ex.getMessage()).isEqualTo("El usuario no existe");
                })
                .verify();

        verify(userRepository).existsByEmailAndByDocument(s.getEmail(), s.getDocument());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(tipoPrestamoRepository, estadoPrestamoRepository, solicitudRepository, notificacionPublisher);
    }

    @Test
    void saveSolicitud_debeFallar_cuandoTipoPrestamoNoExiste() {
        // Arrange
        Solicitud s = buildSolicitudDemo();

        when(userRepository.existsByEmailAndByDocument(s.getEmail(), s.getDocument()))
                .thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(s.getIdTipoPrestamo()))
                .thenReturn(Mono.empty());

        // Act
        Mono<Solicitud> result = useCase.saveSolicitud(s);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(ex.getMessage()).isEqualTo("El tipo de préstamo no existe");
                })
                .verify();

        verify(userRepository).existsByEmailAndByDocument(s.getEmail(), s.getDocument());
        verify(tipoPrestamoRepository).findById(s.getIdTipoPrestamo());
        verifyNoInteractions(estadoPrestamoRepository, solicitudRepository, notificacionPublisher);
    }

    @Test
    void saveSolicitud_debeFallar_cuandoEstadoPendienteNoConfigurado() {
        // Arrange
        Solicitud s = buildSolicitudDemo();

        when(userRepository.existsByEmailAndByDocument(s.getEmail(), s.getDocument()))
                .thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(s.getIdTipoPrestamo()))
                .thenReturn(Mono.just(mock(TipoPrestamo.class)));
        when(estadoPrestamoRepository.findIdByNombre("PENDIENTE DE REVISIÓN"))
                .thenReturn(Mono.empty());

        // Act
        Mono<Solicitud> result = useCase.saveSolicitud(s);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(ex.getMessage())
                            .contains("No está configurado el estado 'PENDIENTE DE REVISIÓN'");
                })
                .verify();

        verify(userRepository).existsByEmailAndByDocument(s.getEmail(), s.getDocument());
        verify(tipoPrestamoRepository).findById(s.getIdTipoPrestamo());
        verify(estadoPrestamoRepository).findIdByNombre("PENDIENTE DE REVISIÓN");
        verifyNoInteractions(solicitudRepository, notificacionPublisher);
    }

    @Test
    void saveSolicitud_debeGuardarConEstadoPendiente() {
        // Arrange
        Solicitud s = buildSolicitudDemo();
        long idEstadoPendiente = 10L;

        when(userRepository.existsByEmailAndByDocument(s.getEmail(), s.getDocument()))
                .thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(s.getIdTipoPrestamo()))
                .thenReturn(Mono.just(mock(TipoPrestamo.class)));
        when(estadoPrestamoRepository.findIdByNombre("PENDIENTE DE REVISIÓN"))
                .thenReturn(Mono.just(idEstadoPendiente));
        // Devolver lo que llega al save:
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // Act
        Mono<Solicitud> result = useCase.saveSolicitud(s);

        // Assert
        StepVerifier.create(result)
                .assertNext(saved -> assertThat(saved.getIdEstado()).isEqualTo(idEstadoPendiente))
                .verifyComplete();

        ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepository).save(captor.capture());
        assertThat(captor.getValue().getIdEstado()).isEqualTo(idEstadoPendiente);
    }

    // ---------- getSolictudesAndUsuarios ----------

    @Test
    void getSolictudesAndUsuarios_debeDelegarEnRepositorio() {
        // Arrange
        Admin adminMock = mock(Admin.class);
        when(solicitudRepository.getAdminsByEstadoNombreIn(anyList()))
                .thenReturn(Flux.just(adminMock));

        // Act
        Flux<Admin> result = useCase.getSolictudesAndUsuarios();

        // Assert
        StepVerifier.create(result)
                .expectNext(adminMock)
                .verifyComplete();

        ArgumentCaptor<List<String>> estadosCap = ArgumentCaptor.forClass(List.class);
        verify(solicitudRepository).getAdminsByEstadoNombreIn(estadosCap.capture());
        assertThat(estadosCap.getValue())
                .containsExactlyInAnyOrder("PENDIENTE DE REVISIÓN", "RECHAZADA", "REVISIÓN MANUAL", "ACEPTADA");
    }

    // ---------- listarPendientes ----------

    @Test
    void listarPendientes_debeDelegarYRetornarPagina() {
        // Arrange
        int page = 0, size = 20;
        String sort = "fecha,desc";
        AdminFilters filters = mock(AdminFilters.class);
        PageResponse<Admin> pageResponse = mock(PageResponse.class);

        when(solicitudRepository.pageAdminsByEstado(anyList(), eq(page), eq(size), eq(sort), eq(filters)))
                .thenReturn(Mono.just(pageResponse));

        // Act
        Mono<PageResponse<Admin>> result = useCase.listarPendientes(page, size, sort, filters);

        // Assert
        StepVerifier.create(result)
                .expectNext(pageResponse)
                .verifyComplete();

        ArgumentCaptor<List<String>> estadosCap = ArgumentCaptor.forClass(List.class);
        verify(solicitudRepository).pageAdminsByEstado(estadosCap.capture(), eq(page), eq(size), eq(sort), eq(filters));
        assertThat(estadosCap.getValue()).containsExactlyInAnyOrder("PENDIENTE DE REVISIÓN", "REVISIÓN MANUAL");
    }

    // ---------- ejecutar ----------

    @Test
    void ejecutar_debeFallar_siIdEsNull() {
        // Arrange

        // Act
        Mono<Solicitud> result = useCase.ejecutar(null, "ACEPTADA");

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(ex.getMessage()).isEqualTo("Id de solicitud es requerido");
                })
                .verify();

        verifyNoInteractions(solicitudRepository, estadoPrestamoRepository, notificacionPublisher);
    }

    @Test
    void ejecutar_debeFallar_siEstadoInvalido() {
        // Arrange

        // Act
        Mono<Solicitud> result = useCase.ejecutar(5L, "OTRO");

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(ex.getMessage()).isEqualTo("Estado inválido. Use ACEPTADA o RECHAZADA");
                })
                .verify();

        verifyNoInteractions(solicitudRepository, estadoPrestamoRepository, notificacionPublisher);
    }

    @Test
    void ejecutar_debeFallar_siSolicitudNoExiste() {
        // Arrange
        long id = 7L;
        when(solicitudRepository.findById(id)).thenReturn(Mono.empty());

        // Act
        Mono<Solicitud> result = useCase.ejecutar(id, "ACEPTADA");

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(ex.getMessage()).isEqualTo("Solicitud no encontrada");
                })
                .verify();

        verify(solicitudRepository).findById(id);
        verifyNoMoreInteractions(solicitudRepository);
        verifyNoInteractions(estadoPrestamoRepository, notificacionPublisher);
    }

    @Test
    void ejecutar_debeFallar_siEstadoObjetivoNoConfigurado() {
        // Arrange
        long id = 8L;
        Solicitud existente = buildSolicitudDemo().toBuilder().id(id).build();

        when(solicitudRepository.findById(id)).thenReturn(Mono.just(existente));
        when(estadoPrestamoRepository.findIdByNombre("RECHAZADA")).thenReturn(Mono.empty());

        // Act
        Mono<Solicitud> result = useCase.ejecutar(id, "RECHAZADA");

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(ex.getMessage()).isEqualTo("Estado no configurado: RECHAZADA");
                })
                .verify();

        verify(solicitudRepository).findById(id);
        verify(estadoPrestamoRepository).findIdByNombre("RECHAZADA");
        verifyNoInteractions(notificacionPublisher);
    }

    @Test
    void ejecutar_debeFallar_siUpdateNoModificaFilas() {
        // Arrange
        long id = 9L;
        long idEstado = 33L;
        Solicitud existente = buildSolicitudDemo().toBuilder().id(id).build();

        when(solicitudRepository.findById(id)).thenReturn(Mono.just(existente));
        when(estadoPrestamoRepository.findIdByNombre("ACEPTADA")).thenReturn(Mono.just(idEstado));
        when(solicitudRepository.updateEstadoById(id, idEstado)).thenReturn(Mono.just(0));

        // Act
        Mono<Solicitud> result = useCase.ejecutar(id, "ACEPTADA");

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(ex.getMessage()).isEqualTo("No se pudo actualizar el estado");
                })
                .verify();

        verify(solicitudRepository).findById(id);
        verify(estadoPrestamoRepository).findIdByNombre("ACEPTADA");
        verify(solicitudRepository).updateEstadoById(id, idEstado);
        verifyNoInteractions(notificacionPublisher);
    }

    @Test
    void ejecutar_debeActualizarYNotificar_ok() {
        // Arrange
        long id = 10L;
        long idEstado = 44L;
        Solicitud existente = buildSolicitudDemo().toBuilder().id(id).build();

        when(solicitudRepository.findById(id)).thenReturn(Mono.just(existente));
        when(estadoPrestamoRepository.findIdByNombre("ACEPTADA")).thenReturn(Mono.just(idEstado));
        when(solicitudRepository.updateEstadoById(id, idEstado)).thenReturn(Mono.just(1));
        when(notificacionPublisher.publicar(any())).thenReturn(Mono.empty());

        // Act
        Mono<Solicitud> result = useCase.ejecutar(id, "  aceptada  "); // prueba normalización

        // Assert
        StepVerifier.create(result)
                .assertNext(sol -> {
                    assertThat(sol.getId()).isEqualTo(id);
                    assertThat(sol.getIdEstado()).isEqualTo(idEstado);
                })
                .verifyComplete();

        verify(notificacionPublisher, times(1)).publicar(any());
    }

    @Test
    void ejecutar_debeActualizarAunSiNotificacionFalla() {
        // Arrange
        long id = 11L;
        long idEstado = 55L;
        Solicitud existente = buildSolicitudDemo().toBuilder().id(id).build();

        when(solicitudRepository.findById(id)).thenReturn(Mono.just(existente));
        when(estadoPrestamoRepository.findIdByNombre("RECHAZADA")).thenReturn(Mono.just(idEstado));
        when(solicitudRepository.updateEstadoById(id, idEstado)).thenReturn(Mono.just(1));
        when(notificacionPublisher.publicar(any())).thenReturn(Mono.error(new RuntimeException("SNS caído")));

        // Act
        Mono<Solicitud> result = useCase.ejecutar(id, "RECHAZADA");

        // Assert
        StepVerifier.create(result)
                .assertNext(sol -> {
                    assertThat(sol.getId()).isEqualTo(id);
                    assertThat(sol.getIdEstado()).isEqualTo(idEstado);
                })
                .verifyComplete();

        verify(notificacionPublisher, times(1)).publicar(any());
    }

    // ---------- helpers ----------

    private Solicitud buildSolicitudDemo() {
        // Ajusta este builder/setters a tu modelo real
        // Ejemplo con builder típico de Lombok:
        return Solicitud.builder()
                .id(1L)
                .email("user@test.com")
                .document("CC-123")
                .idTipoPrestamo(5L)
                .idEstado(null)
                .build();
    }
}