package br.com.microservices.orchestrared.productvalidationservice.core.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrared.productvalidationservice.core.utils.JsonUtil;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@Component
@AllArgsConstructor
public class ProductValidationConsumer {

  private final JsonUtil jsonUtil;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.product-validation-success}"
  )
  public void consumerSuccessEvent(String payload){
    log.info("Recevendo evento {} de product-validation-success", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.product-validation-fail}"
  )
  public void consumerFailEvent(String payload){
    log.info("Recevendo evento {} de product-validation-fail", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }


}
