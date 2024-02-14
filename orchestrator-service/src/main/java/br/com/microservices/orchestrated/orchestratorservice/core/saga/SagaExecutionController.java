package br.com.microservices.orchestrated.orchestratorservice.core.saga;

import br.com.microservices.orchestrated.orchestratorservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orchestratorservice.core.dto.Event;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static br.com.microservices.orchestrated.orchestratorservice.core.saga.SagaHandler.*;
import static java.lang.String.format;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {

    private static final String LOG_FORMAT = "ORDER ID: %s | TRANSACTION ID: %s | EVENT ID: %s";

    public ETopics getNextTopic(Event event){
        if(isEmpty(event.getStatus()) || isEmpty(event.getSource())){
            throw new ValidationException("Source and Status must be informed!!");
        }

        var topic = findTopicBySourceAndStatus(event);

        logCurrentSaga(event, topic);

        return topic;
    }

    private ETopics findTopicBySourceAndStatus(Event event){
        return (ETopics) (Arrays.stream(SAGA_HANDLER)
                .filter(e -> e[EVENT_SOURCE_INDEX].equals(event.getSource()) && e[SAGA_STATUS_INDEX].equals(event.getStatus()))
                .map(i -> i[TOPIC_INDEX])
                .findFirst()
                .orElseThrow(() -> new ValidationException("Topico não encontrado!")));
    }
    
    private void logCurrentSaga(Event event, ETopics topic){
        var sagaId = createSagaId(event);
        var source = event.getSource();
        switch (event.getStatus()){
            case SUCCESS -> log.info(">>> CURRENT SAGA: {} | SUCCESS | NEXT TOPIC: {} | {}",
                    source, topic, sagaId);
            case ROLLBACK_PENDING -> log.info(">>> CURRENT SAGA: {} | ROLLBACK TO CURRENT SERVICE | NEXT TOPIC: {} | {}",
                    source, topic, sagaId);
            case FAIL -> log.info(">>> CURRENT SAGA: {} | FAIL TO PREVIOUS SERVICE | NEXT TOPIC: {} | {}",
                    source, topic, sagaId);
        }
    }

    private String createSagaId(Event event){
        return  format(LOG_FORMAT,
                event.getPayload().getId(),
                event.getTransactionId(),
                event.getId());
    }
}
