package br.com.microservices.choreography.productvalidationservice.core.saga;

import br.com.microservices.choreography.productvalidationservice.core.dto.Event;
import br.com.microservices.choreography.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.choreography.productvalidationservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class SagaExecutionController {

    private static final String SAGA_LOG_ID = "ORDER ID: %s | TRANSACTION ID: %s | EVENT ID: %s";

    @Value("${spring.kafka.topic.product-validation-fail}")
    private String productValidationFailTopic;

    @Value("${spring.kafka.topic.payment-success}")
    private String paymentSuccessTopic;

    @Value("${spring.kafka.topic.notify-ending}")
    private String notifyEndingTopic;

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;

    public void handleSaga(Event event){
        switch (event.getStatus()){
            case SUCCESS -> handleSuccess(event);
            case ROLLBACK_PENDING -> handleRollbackPending(event);
            case FAIL -> handleFail(event);
        }
    }

    private void handleSuccess(Event event){
        log.info(">>> CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}",
                event.getSource(),
                paymentSuccessTopic,
                createSagaId(event));

        sendEvent(event, paymentSuccessTopic);

    }

    private void handleRollbackPending(Event event){
        log.info(">>> CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}",
                event.getSource(),
                productValidationFailTopic,
                createSagaId(event));
        sendEvent(event, productValidationFailTopic);
    }

    private void handleFail(Event event){
        log.info(">>> CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}",
                event.getSource(),
                notifyEndingTopic,
                createSagaId(event));
        sendEvent(event, notifyEndingTopic);
    }

    private void sendEvent(Event event, String topic){
        kafkaProducer.sendEvent(jsonUtil.toJson(event), topic);
    }

    private String createSagaId(Event event){
        return format(SAGA_LOG_ID,
                event.getPayload().getId(),
                event.getTransactionId(),
                event.getId());
    }
}