package co.com.pragma.r2dbc.capacidad;

import co.com.pragma.model.capacidad.CalcularCapacidadDecision;
import co.com.pragma.model.estadoprestamo.gateways.EstadoPrestamoRepository;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import com.fasterxml.jackson.core.JsonProcessingException;           // <- jackson estándar
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsCapacidadResultadoConsumer {
    private final SqsAsyncClient sqs;
    private final ObjectMapper mapper;
    private final SolicitudRepository solicitudRepo;
    private final EstadoPrestamoRepository estadoRepo;

    @Value("${SQS_CAPACIDAD_RESULTADOS_URL}")
    private String resultadosQueue;

    @PostConstruct
    public void start() {
        receiveLoop().repeat().subscribe();
    }

    private Mono<Void> receiveLoop() {
        return Mono.fromFuture(
                        sqs.receiveMessage(ReceiveMessageRequest.builder()
                                .queueUrl(resultadosQueue)
                                .waitTimeSeconds(20)
                                .maxNumberOfMessages(5)
                                .build()))
                .flatMapMany(resp -> Flux.fromIterable(resp.messages()))
                .flatMap(this::processMessage)
                .onErrorContinue((e, o) -> log.warn("Error en loop SQS", e))
                .then()
                .delayElement(Duration.ofSeconds(1));
    }

    private Mono<Void> processMessage(Message m) {
        return Mono.fromCallable(() -> mapper.readValue(m.body(), CalcularCapacidadDecision.class))
                .flatMap(dec -> {
                    String estado = switch (dec.getDecision()) {
                        case "APROBADO" -> "ACEPTADA";
                        case "RECHAZADO" -> "RECHAZADA";
                        default -> "REVISIÓN MANUAL";
                    };

                    return estadoRepo.findIdByNombre(estado)
                            .flatMap(id -> solicitudRepo.updateEstadoById(dec.getIdSolicitud(), id))
                            .then(Mono.fromFuture(
                                    sqs.deleteMessage(DeleteMessageRequest.builder()
                                            .queueUrl(resultadosQueue)
                                            .receiptHandle(m.receiptHandle())
                                            .build())))
                            .then()
                            .doOnSuccess(v -> log.info("[Capacidad][Resultado] id={} {}", dec.getIdSolicitud(), dec.getDecision()));
                })
                .onErrorResume(JsonProcessingException.class, e -> {
                    log.error("JSON inválido, eliminando msg: {}", m.body(), e);
                    return Mono.fromFuture(
                                    sqs.deleteMessage(DeleteMessageRequest.builder()
                                            .queueUrl(resultadosQueue)
                                            .receiptHandle(m.receiptHandle())
                                            .build()))
                            .then();
                });
    }
}
