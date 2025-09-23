package co.com.pragma.r2dbc.capacidad;

import co.com.pragma.model.capacidad.CalcularCapacidadRequest;
import co.com.pragma.model.gateway.CapacidadPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsCapacidadPublisher implements CapacidadPublisher {

    private final SqsAsyncClient sqs;
    private final ObjectMapper mapper;

    @Value("${SQS_CAPACIDAD_URL}")
    private String queueUrl;

    public Mono<Void> publicar(CalcularCapacidadRequest request) {
        return Mono.fromCallable(() -> mapper.writeValueAsString(request))
                .flatMap(body -> Mono.fromFuture(
                        sqs.sendMessage(SendMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .messageBody(body)
                                .messageAttributes(Map.of(
                                        "idSolicitud", MessageAttributeValue.builder()
                                                .dataType("Number").stringValue(String.valueOf(request.getIdSolicitud())).build(),
                                        "email", MessageAttributeValue.builder()
                                                .dataType("String").stringValue(request.getEmail()).build()
                                ))
                                .build())
                ))
                .doOnSuccess(r -> log.info("[Capacidad] Encolado id={} msgId={}",
                        request.getIdSolicitud(), r.messageId()))
                .then();
    }
}
