package br.com.microservices.orchestrared.orchestratorservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrared.orchestratorservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorConsumer {

  private final JsonUtil jsonUtil;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.start-saga}"
  )
  public void consumerStartSagaEvent(String payload){
    log.info("Recevendo evento {} de start-saga", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.orchestrator}"
  )
  public void consumerOrchestratorEvent(String payload){
    log.info("Recevendo evento {} de orchestrator", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.finish-success}"
  )
  public void consumerFinishSuccessEvent(String payload){
    log.info("Recevendo evento {} de finish-success", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.finish-fail}"
  )
  public void consumerFinishFailEvent(String payload){
    log.info("Recevendo evento {} de finish-fail", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }


}
