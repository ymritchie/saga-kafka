package br.com.microservices.orchestrated.orchestratorservice.core.service;

import br.com.microservices.orchestrated.orchestratorservice.core.dto.Event;
import br.com.microservices.orchestrated.orchestratorservice.core.dto.History;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;
import br.com.microservices.orchestrated.orchestratorservice.core.producer.SagaOrchestratorProducer;
import br.com.microservices.orchestrated.orchestratorservice.core.saga.SagaExecutionController;
import br.com.microservices.orchestrated.orchestratorservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource.ORCHESTRATOR;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus.FAIL;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus.SUCCESS;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics.NOTIFY_ENDING;

@Service
@AllArgsConstructor
@Slf4j
public class OrchestratorService {

    private final SagaOrchestratorProducer producer;
    private final JsonUtil jsonUtil;
    private final SagaExecutionController sagaExecutionController;

    public void startSaga(Event event){
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        var topic = getTopic(event);
        log.info("SAGA STARTED!");
        addHistory(event, "Saga started!");
        sendToProducerWithTopic(event, topic);
    }

    public void finishSagaSuccess(Event event){
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        log.info("SAGA FINISHED SUCCESSFULLY, EVENT: {}", event.getId());
        addHistory(event, "Saga Finished Success!");
        notifyFinishedSaga(event);
    }

    public void finishSagaFail(Event event){
        event.setSource(ORCHESTRATOR);
        event.setStatus(FAIL);
        log.info("SAGA FINISHED WITH ERRORS, EVENT: {}", event.getId());
        addHistory(event, "Saga Finished With Errors!");
        notifyFinishedSaga(event);
    }

    public void continueSaga(Event event){
        var topic = getTopic(event);
        log.info("SAGA CONTINUING FOR EVENT {}", event.getId());
        sendToProducerWithTopic(event, topic);
    }

    private void sendToProducerWithTopic(Event event, ETopics topic) {
        producer.sendEvent(topic.getTopic(), jsonUtil.toJson(event));
    }

    private ETopics getTopic(Event event){
        return sagaExecutionController.getNextTopic(event);
    }

    private void addHistory(Event event, String message){
        var history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addToHistory(history);
    }

    private void notifyFinishedSaga(Event event){
        producer.sendEvent(NOTIFY_ENDING.getTopic(), jsonUtil.toJson(event));
    }

}
