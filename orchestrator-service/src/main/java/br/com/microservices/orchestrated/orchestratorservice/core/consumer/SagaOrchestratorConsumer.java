package br.com.microservices.orchestrated.orchestratorservice.core.consumer;

import br.com.microservices.orchestrated.orchestratorservice.core.service.OrchestratorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.orchestratorservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorConsumer {

  private final JsonUtil jsonUtil;
  private final OrchestratorService orchestratorService;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.start-saga}"
  )
  public void consumerStartSagaEvent(String payload){
    log.info("Recevendo evento {} de start-saga", payload);
    var event = jsonUtil.toEvent(payload);
    orchestratorService.startSaga(event);
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.orchestrator}"
  )
  public void consumerOrchestratorEvent(String payload){
    log.info("Recevendo evento {} de orchestrator", payload);
    var event = jsonUtil.toEvent(payload);
    orchestratorService.continueSaga(event);
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.finish-success}"
  )
  public void consumerFinishSuccessEvent(String payload){
    log.info("Recevendo evento {} de finish-success", payload);
    var event = jsonUtil.toEvent(payload);
    orchestratorService.finishSagaSuccess(event);
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.finish-fail}"
  )
  public void consumerFinishFailEvent(String payload){
    log.info("Recevendo evento {} de finish-fail", payload);
    var event = jsonUtil.toEvent(payload);
    orchestratorService.finishSagaFail(event);
  }


}
