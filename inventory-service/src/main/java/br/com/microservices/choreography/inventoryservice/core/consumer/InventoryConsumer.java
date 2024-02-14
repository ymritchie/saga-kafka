package br.com.microservices.choreography.inventoryservice.core.consumer;

import br.com.microservices.choreography.inventoryservice.core.service.InventoryService;
import br.com.microservices.choreography.inventoryservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {

  private final JsonUtil jsonUtil;

  private final InventoryService inventoryService;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.inventory-success}"
  )
  public void consumerSuccessEvent(String payload){
    log.info("Recevendo evento {} de inventory-success", payload);
    var event = jsonUtil.toEvent(payload);
    inventoryService.updateInventory(event);
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.inventory-fail}"
  )
  public void consumerFailEvent(String payload){
    log.info("Recevendo evento {} de inventory-fail", payload);
    var event = jsonUtil.toEvent(payload);
    inventoryService.rollbackInventory(event);
  }


}
