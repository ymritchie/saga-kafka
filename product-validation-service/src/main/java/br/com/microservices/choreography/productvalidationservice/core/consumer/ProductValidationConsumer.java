package br.com.microservices.choreography.productvalidationservice.core.consumer;

import br.com.microservices.choreography.productvalidationservice.core.service.ProductValidationService;
import br.com.microservices.choreography.productvalidationservice.core.utils.JsonUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ProductValidationConsumer {

  private final ProductValidationService productValidationService;
  private final JsonUtil jsonUtil;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.product-validation-success}"
  )
  public void consumerSuccessEvent(String payload){
    log.info("Recevendo evento {} de product-validation-success", payload);
    var event = jsonUtil.toEvent(payload);
    productValidationService.validarProductosExistentes(event);
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.product-validation-fail}"
  )
  public void consumerFailEvent(String payload){
    log.info("Recevendo evento {} de product-validation-fail", payload);
    var event = jsonUtil.toEvent(payload);
    productValidationService.rollbackEvent(event);
  }


}
