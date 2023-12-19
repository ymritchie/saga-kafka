package br.com.microservices.orchestrared.inventoryservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.inventoryservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {

  private final JsonUtil jsonUtil;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.inventory-success}"
  )
  public void consumerSuccessEvent(String payload){
    log.info("Recevendo evento {} de inventory-success", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.inventory-fail}"
  )
  public void consumerFailEvent(String payload){
    log.info("Recevendo evento {} de inventory-fail", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }


}
