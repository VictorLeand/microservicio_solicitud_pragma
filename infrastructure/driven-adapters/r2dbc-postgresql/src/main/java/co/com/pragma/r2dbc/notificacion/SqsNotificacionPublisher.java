package co.com.pragma.r2dbc.notificacion;

import co.com.pragma.model.lambdas.NotificacionMensaje;
import co.com.pragma.model.gateway.NotificacionPublisher;
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
public class SqsNotificacionPublisher implements NotificacionPublisher {

    private final SqsAsyncClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${SQS_NOTIFICACIONES_URL}")
    private String queueUrl;

    @Override
    public Mono<Void> publicar(NotificacionMensaje mensaje) {
        return Mono.defer(() -> {
            // Body liviano (la Lambda no lo usa para formatear).
            // Puedes dejar un body corto o JSON mínimo; evita null.
            String body = mensaje.getMensaje() != null && !mensaje.getMensaje().isBlank()
                    ? mensaje.getMensaje()
                    : "{\"estado\":\"" + (mensaje.getEstado() == null ? "" : mensaje.getEstado()) + "\"}";

            SendMessageRequest req = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .messageAttributes(Map.of(
                            "subject", MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue(mensaje.getAsunto() != null ? mensaje.getAsunto() : "Notificación de estado de prestamo")
                                    .build(),
                            "email", MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue(mensaje.getEmail())
                                    .build(),
                            "estado", MessageAttributeValue.builder()          // <--- CLAVE: la Lambda lee este atributo
                                    .dataType("String")
                                    .stringValue(mensaje.getEstado())
                                    .build()
                    ))
                    .build();

            return Mono.fromFuture(sqsClient.sendMessage(req))
                    .doOnNext(res -> log.info("[SQS] Mensaje enviado a {} con messageId={}", queueUrl, res.messageId()))
                    .then();
        }).doOnError(e -> log.error("[SQS] Error al enviar mensaje a {}", queueUrl, e));
    }
}